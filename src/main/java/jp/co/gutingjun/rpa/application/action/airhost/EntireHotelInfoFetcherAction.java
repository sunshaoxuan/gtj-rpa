package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.rpa.common.CommonUtils;

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
  protected Object doAction(Object inputData) {
    Map<String, Object> hotelInfoMap = (Map<String, Object>) super.doAction(inputData);
    if (hotelInfoMap.size() > 0) {
      ((List) hotelInfoMap.get("result"))
          .forEach(
              hotel -> {
                String id = (String) ((Map<String, Object>) hotel).get("id");

                // HotelDetail
                HotelDetailFetcherAction hotelDetailAction = new HotelDetailFetcherAction();
                CommonUtils.mapPutAll(hotelDetailAction.getContext(), this.getContext());
                CommonUtils.mapPutAll(hotelDetailAction.getWebContext(), this.getWebContext());
                hotelDetailAction.setHouseId(Long.parseLong(id));
                hotelDetailAction.setURL(
                    "https://cloud.airhost.co/en/houses/" + TAG_HOUSEID + ".json");
                Map hotelDetailMap = (Map) hotelDetailAction.execute();
                CommonUtils.mapPutAll((Map<String, Object>) hotel, hotelDetailMap);

                CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                // RoomType
                RoomTypeFetcherAction roomTypeAction = new RoomTypeFetcherAction();
                CommonUtils.mapPutAll(
                    roomTypeAction.getWebContext(), hotelDetailAction.getWebContext());
                CommonUtils.mapPutAll(roomTypeAction.getContext(), hotelDetailAction.getContext());
                roomTypeAction.setHouseId(Long.parseLong(id));
                roomTypeAction.setURL(
                    "https://cloud.airhost.co/en/houses/" + TAG_HOUSEID + "/rooms.json");
                Map roomTypeMap = (Map) roomTypeAction.execute();

                CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                HotelPictureFetcherAction pictureAction = new HotelPictureFetcherAction();
                ((List) roomTypeMap.get("rooms"))
                    .forEach(
                        typeMap -> {
                          String typeid = (String) ((Map) typeMap).get("id");
                          RoomTypeDetailFetcherAction roomTypeDetailAction =
                              new RoomTypeDetailFetcherAction();
                          CommonUtils.mapPutAll(
                              roomTypeDetailAction.getContext(), this.getContext());
                          CommonUtils.mapPutAll(
                              roomTypeDetailAction.getWebContext(), this.getWebContext());
                          roomTypeDetailAction.setHouseId(Long.parseLong(id));
                          roomTypeDetailAction.setRoomTypeId(Long.parseLong(typeid));
                          roomTypeDetailAction.setURL(
                              "https://cloud.airhost.co/en/houses/"
                                  + roomTypeDetailAction.TAG_HOUSEID
                                  + "/rooms/"
                                  + roomTypeDetailAction.TAG_ROOMTYPEID
                                  + ".json");
                          Map roomTypeDetail = (Map) roomTypeDetailAction.execute();
                          CommonUtils.mapPutAll((Map) typeMap, (Map) roomTypeDetail.get("room"));

                          CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                          CommonUtils.mapPutAll(pictureAction.getContext(), this.getContext());
                          CommonUtils.mapPutAll(
                              pictureAction.getWebContext(), this.getWebContext());
                          pictureAction.setHouseId(Long.parseLong(id));
                          pictureAction.setRoomTypeId(Long.parseLong(typeid));
                          pictureAction.setURL(
                              "https://cloud.airhost.co/en/houses/"
                                  + pictureAction.TAG_HOUSEID
                                  + "/rooms/"
                                  + pictureAction.TAG_ROOMTYPEID
                                  + "/photos.json");
                          Map picList = (Map) pictureAction.execute();
                          CommonUtils.mapPutAll((Map) typeMap, picList);
                        });

                Map<String, Object> mapRoomTypeData = new HashMap<>();
                mapRoomTypeData.put("rooms_type", roomTypeMap.get("rooms"));
                CommonUtils.mapPutAll(((Map<String, Object>) hotel), mapRoomTypeData);

                CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                // RoomList
                HotelRoomFetcherAction roomAction = new HotelRoomFetcherAction();
                CommonUtils.mapPutAll(roomAction.getContext(), this.getContext());
                CommonUtils.mapPutAll(roomAction.getWebContext(), this.getWebContext());
                roomAction.setHouseId(Long.parseLong(id));
                roomAction.setURL(
                    "https://cloud.airhost.co/en/houses/"
                        + roomAction.TAG_HOUSEID
                        + "/room_units.json");
                Map roomMap = (Map) roomAction.execute();
                ((Map<String, Object>) hotel).put("rooms", roomMap.get("room_units"));

                CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());

                // HotelPicture
                CommonUtils.mapPutAll(pictureAction.getContext(), this.getContext());
                CommonUtils.mapPutAll(pictureAction.getWebContext(), this.getWebContext());
                pictureAction.setHouseId(Long.parseLong(id));
                pictureAction.setURL(
                    "https://cloud.airhost.co/en/houses/" + TAG_HOUSEID + "/photos.json");
                Map picMap = (Map) pictureAction.execute();
                ((Map<String, Object>) hotel).put("house_pictures", picMap);
              });
    }

    return hotelInfoMap;
  }
}
