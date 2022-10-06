package jp.co.gutingjun.rpa.application.action.airhost;

public class HotelPictureFetcherAction extends DataRESTFetcherActionModel{
    public HotelPictureFetcherAction(){
        getWebContext().put(URL, "https://cloud.airhost.co/en/houses/"+ TAG_HOUSEID + "/photos.json");
        appendDependActionClasses(UserPasswordLoginAction.class);
    }

    @Override
    protected Object doAction(Object inputData) {
        return fetchData();
    }
}
