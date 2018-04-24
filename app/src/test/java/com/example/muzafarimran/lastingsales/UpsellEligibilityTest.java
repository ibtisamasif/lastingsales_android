//package com.example.muzafarimran.lastingsales;
//
//import com.fidosolutions.myaccount.commons.models.network.services.Status;
//import com.fidosolutions.myaccount.commons.network.IWebServicesCallback;
//import com.fidosolutions.myaccount.commons.network.WebServiceResponse;
//import com.fidosolutions.myaccount.uep.services.upsell_eligibility.UpsellEligibilityCallback;
//import com.fidosolutions.myaccount.uep.services.upsell_eligibility.UpsellEligibilityRequest;
//import com.fidosolutions.myaccount.uep.services.upsell_eligibility.UpsellEligibilityResponse;
//
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import retrofit.client.Header;
//import retrofit.client.Response;
//
//import static junit.framework.Assert.assertFalse;
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by kashif on 19/01/2018.
// */
//
//public class UpsellEligibilityTest {
//
//    private String DUMMY_BAN = "00000000";
//    private String DUMMY_CTN = "11111111";
//    private String DUMMY_ORIGIN = "on";
//
//    //expected result code
//    private String StatusCode = "200";
//
//    //expected Upsell Eligibility
//    private boolean upsellEligibility = true;
//
//    private IWebServicesCallback upsellEligibilityCallback = new IWebServicesCallback() {
//
//        @Override
//        public void onSuccess(long requestId, WebServiceResponse webServiceResponse) {
//            if(webServiceResponse instanceof UpsellEligibilityResponse){
//                handleUpsellEligibilityResponse((UpsellEligibilityResponse) webServiceResponse);
//                return;
//            }
//            assertFalse(true);
//        }
//
//        @Override
//        public void onFailure(long requestId, WebServiceResponse webServiceResponse, Throwable err) {
//            assertEquals(true, (err!=null));
//        }
//
//        @Override
//        public void doPreNetwork(long requestId) {}
//
//        @Override
//        public void doPostNetwork(long requestId) {}
//    };
//
//    private void handleUpsellEligibilityResponse(UpsellEligibilityResponse response) {
//        assertEquals(true, (response != null));
//        String resultCode = response.getStatus().getCode();
//        assertEquals(true, resultCode.equals(StatusCode));
//        assertEquals(true, (upsellEligibility == response.getContent().isEligible()));
//    }
//
//    private UpsellEligibilityResponse setResponse(String result, boolean upsellEligibility) {
//        UpsellEligibilityResponse response = new UpsellEligibilityResponse();
//
//        Status status = new Status();
//        status.setCode(_result);
//        response.setStatus(status);
//
//        response.getContent().setEligibility(_upsellEligibility);
//
//        return response;
//    }
//
//    private void setResult(String result, boolean upsellEligibility) {
//        StatusCode = _result;
//        upsellEligibility = _upsellEligibility;
//    }
//
//    private Response setTestResponse(UpsellEligibilityRequest request) {
//        List<Header> headers = new ArrayList<>();
//        Iterator it = request.getHeaders().entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry par = (Map.Entry)it.next();
//            Header h = new Header((String) par.getKey(), (String) par.getValue());
//            headers.add(h);
//        }
//        Response resp = new Response("", 200, "", headers, null);
//        return resp;
//    }
//
//    @Test
//    public void testUpsellEligibility() {
//
//        UpsellEligibilityRequest request = new UpsellEligibilityRequest(DUMMY_BAN, DUMMY_CTN, DUMMY_ORIGIN);
//        Response rResp = setTestResponse(request);
//
//        UpsellEligibilityCallback callback = new UpsellEligibilityCallback(upsellEligibilityCallback, request);
//
//        UpsellEligibilityResponse response = setResponse(StatusCode, true);
//        setResult(StatusCode, true);
//        callback.success(response, rResp);
//
//    }
//
//}