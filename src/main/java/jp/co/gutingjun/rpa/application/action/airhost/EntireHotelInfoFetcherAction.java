package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.common.util.MapUtils;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.common.RPAConst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntireHotelInfoFetcherAction extends HotelFetcherAction {
  /** 启用访问休眠机制时，最大休眠秒数</br> （休眠时间随机生成，不超过最大休眠秒数） */
  private int maxSleepSeconds = 1;

  public int getMaxSleepSeconds() {
    if (maxSleepSeconds < 1) {
      maxSleepSeconds = 1;
    }

    return maxSleepSeconds;
  }

  public void setMaxSleepSeconds(int maxSleepSeconds) {
    this.maxSleepSeconds = maxSleepSeconds;
  }

  @Override
  protected Object doAction() {
    if (this.getContext().containsKey("HOUSEID")) {
      super.setHouseId(Long.parseLong((String) this.getContext().get("HOUSEID")));
    }
    boolean result = (boolean) super.doAction();
    if (result) {
      Map<String, Object> hotelInfoMap = (Map<String, Object>) super.getOutputData();
      if (hotelInfoMap.size() > 0) {
        ((List) hotelInfoMap.get("result"))
            .forEach(
                hotel -> {
                  String id = (String) ((Map<String, Object>) hotel).get("id");

                  // HotelDetail
                  HotelDetailFetcherAction hotelDetailAction = new HotelDetailFetcherAction();
                  hotelDetailAction.setParentContainer(this.getParentContainer());
                  MapUtils.mapPutAll(hotelDetailAction.getContext(), this.getContext());
                  hotelDetailAction.setHouseId(Long.parseLong(id));
                  hotelDetailAction.setURL(
                      "https://cloud.airhost.co/en/houses/" + RPAConst.TAG_HOUSEID + ".json");
                  hotelDetailAction.execute();
                  Map hotelDetailMap = (Map) hotelDetailAction.getOutputData();
                  MapUtils.mapPutAll((Map<String, Object>) hotel, hotelDetailMap);

                  CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                  // RoomType
                  RoomTypeFetcherAction roomTypeAction = new RoomTypeFetcherAction();
                  roomTypeAction.setParentContainer(this.getParentContainer());
                  MapUtils.mapPutAll(roomTypeAction.getContext(), hotelDetailAction.getContext());
                  roomTypeAction.setHouseId(Long.parseLong(id));
                  roomTypeAction.setURL(
                      "https://cloud.airhost.co/en/houses/" + RPAConst.TAG_HOUSEID + "/rooms.json");
                  roomTypeAction.execute();
                  Map roomTypeMap =
                      (Map)
                          (roomTypeAction.getOutputData() != null
                                  && ((Map) roomTypeAction.getOutputData()).containsKey(id)
                              ? ((Map<?, ?>) roomTypeAction.getOutputData()).get(id)
                              : roomTypeAction.getOutputData());

                  CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                  HotelPictureFetcherAction pictureAction = new HotelPictureFetcherAction();
                  pictureAction.setParentContainer(this.getParentContainer());
                  ((List) roomTypeMap.get("rooms"))
                      .forEach(
                          typeMap -> {
                            String typeid = (String) ((Map) typeMap).get("id");
                            RoomTypeDetailFetcherAction roomTypeDetailAction =
                                new RoomTypeDetailFetcherAction();
                            roomTypeDetailAction.setParentContainer(this.getParentContainer());
                            MapUtils.mapPutAll(
                                roomTypeDetailAction.getContext(), this.getContext());
                            MapUtils.mapPutAll(
                                roomTypeDetailAction.getContext(), this.getContext());
                            roomTypeDetailAction.setHouseId(Long.parseLong(id));
                            roomTypeDetailAction.setRoomTypeId(Long.parseLong(typeid));
                            roomTypeDetailAction.setURL(
                                "https://cloud.airhost.co/en/houses/"
                                    + RPAConst.TAG_HOUSEID
                                    + "/rooms/"
                                    + RPAConst.TAG_ROOMTYPEID
                                    + ".json");
                            roomTypeDetailAction.execute();
                            Map roomTypeDetail = (Map) roomTypeDetailAction.getOutputData();
                            MapUtils.mapPutAll((Map) typeMap, (Map) roomTypeDetail.get("room"));
                            CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());
                            MapUtils.mapPutAll(pictureAction.getContext(), this.getContext());
                            pictureAction.setHouseId(Long.parseLong(id));
                            pictureAction.setRoomTypeId(Long.parseLong(typeid));
                            pictureAction.setURL(
                                "https://cloud.airhost.co/en/houses/"
                                    + RPAConst.TAG_HOUSEID
                                    + "/rooms/"
                                    + RPAConst.TAG_ROOMTYPEID
                                    + "/photos.json");
                            pictureAction.execute();
                            Map picList = (Map) pictureAction.getOutputData();
                            MapUtils.mapPutAll((Map) typeMap, picList);
                          });

                  Map<String, Object> mapRoomTypeData = new HashMap<>();
                  mapRoomTypeData.put("rooms_type", roomTypeMap.get("rooms"));
                  MapUtils.mapPutAll(((Map<String, Object>) hotel), mapRoomTypeData);

                  CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                  // RoomList
                  HotelRoomFetcherAction roomAction = new HotelRoomFetcherAction();
                  roomAction.setParentContainer(this.getParentContainer());
                  MapUtils.mapPutAll(roomAction.getContext(), this.getContext());
                  roomAction.setHouseId(Long.parseLong(id));
                  roomAction.setURL(
                      "https://cloud.airhost.co/en/houses/"
                          + RPAConst.TAG_HOUSEID
                          + "/room_units.json");
                  roomAction.execute();
                  Map roomMap =
                      roomAction.getOutputData() != null
                              && ((Map) roomAction.getOutputData()).containsKey(id)
                          ? (Map) ((Map) roomAction.getOutputData()).get(id)
                          : (Map) roomAction.getOutputData();
                  ((Map<String, Object>) hotel).put("rooms", roomMap.get("room_units"));

                  CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                  // HotelPicture
                  MapUtils.mapPutAll(pictureAction.getContext(), this.getContext());
                  pictureAction.setHouseId(Long.parseLong(id));
                  pictureAction.setURL(
                      "https://cloud.airhost.co/en/houses/"
                          + RPAConst.TAG_HOUSEID
                          + "/photos.json");
                  pictureAction.execute();
                  Map picMap = (Map) pictureAction.getOutputData();
                  ((Map<String, Object>) hotel).put("house_pictures", picMap);
                });
      }

      setOutputData(hotelInfoMap);
      return hotelInfoMap.size() > 0 && hotelInfoMap.containsKey("result");
    }

    return false;
  }
}
