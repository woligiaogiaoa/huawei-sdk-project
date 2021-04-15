package com.batam.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.batam.sdk.data.pay.OrderNumberBean;
import com.batam.sdk.data.pay.HuaweiPayParam;
import com.batam.sdk.http.JsonCallback;
import com.batam.sdk.http.LzyResponse;
import com.batam.sdk.http.SimpleResponse;
import com.batam.sdk.util.OrderUtil;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.IapClient;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseResult;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.IsEnvReadyResult;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.iap.entity.OwnedPurchasesReq;
import com.huawei.hms.iap.entity.OwnedPurchasesResult;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseIntentResult;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.iap.util.IapClientHelper;
import com.huawei.hms.support.api.client.Status;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.TreeMap;

import static com.batam.sdk.SDKManager.ORDER_CREATE;

public class PayDelegate {

    private String TAG="PAY_DELEGATE";
   // private final String PAY_KEY="MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAlhOL2g5fxK4IrfGUzSKLxG0h4iLC8DcFNiRw4jUs/s0SeZPAg0duUzyjNzEsylQkHeb991ImpJjCg6L1KGQWbBJm1YSemlKwF3SWNVvdfW3x6qQryjUaAe3LYfe0xXTrtPiVPTswZj5Yqs5SqsMchcxAm9r9Cs2E/S2S1VHmiCziLGIrwz5Dvek5xT0ODKYGaFsi62QvNIJtVDJN5fWHAz7ASg4YkVQeTv2NWe/DZBvmKexkMUozmvNUQU+mqY3NV8Jup6yF+GvBCK83LtQmrq1AYciYldxUKLCQVdQZHmXhQrhRU0Ui2LFbcPhSpOPvOsW0TfVUG0l0HsGe7n0H9H2gJfQzsOtfr5UscJ/jtviYzOKkGfJ3k/NEhMUXQTrnAEUxchTL58v2vb16RJkb3Hyv/vUtSm3dCbF5fYKW5hkq5MbsIoVG0IP8rc13akjmvVAZveUxltIqkKehVg10nletkW0C8Cx5Pw8BgpU/fEgK9PQ21yKKljUyAt3uAwRJAgMBAAE=";
    private final int REQ_CODE_BUY=10001;
    private final int REQ_CODE_LOGIN=10002;


    public void h5OrderJsonPay(String orderJson){ //需要游戏传过来 huawei 的 productId
        try {
            JSONObject jsonObject = new JSONObject(orderJson);
            String gameNum = jsonObject.optString("game_num", "");
            String amount = jsonObject.optString("value", "");
            //String slug = jsonObject.optString("slug", "");
            String productName = jsonObject.optString("props_name", "");
            String roleName = jsonObject.optString("role_name", "");
            String roleId = jsonObject.optString("role_id", "");
            String serverId = jsonObject.optString("server_id", "");
            String serverName = jsonObject.optString("server_name", "");
                String productID = jsonObject.optString("productID", "-1");
            String callbackUrl = jsonObject.optString("callback_url", "");
            String extendData = jsonObject.optString("extend_data", "");
            HuaweiPayParam huaweiPayParam = new HuaweiPayParam.Builder()
                    .gameOrderNum(gameNum)
                    .price(amount)
                    .productName(productName)
                    .roleName(roleName)
                    .roleID(roleId)
                    .serverID(serverId)
                    .serverName(serverName)
                    .callbackUrl(callbackUrl)
                    .productId(productID)
                    .extendData(extendData)
                    .build();
            paramsPay(huaweiPayParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void paramsPay(HuaweiPayParam platformPayParam) {
        Log.e(TAG, "paramsPay: "+platformPayParam.toString() );
        TreeMap paramsMap =  new TreeMap<String, String>();
        paramsMap.put("game_num", platformPayParam.getGameOrderNum());
        paramsMap.put("value",platformPayParam.getPrice()); //分
        paramsMap.put("props_name",platformPayParam.getProductName());
        paramsMap.put("callback_url",platformPayParam.getCallbackUrl());
        paramsMap.put("extend_data",platformPayParam.getExtendData());
        paramsMap.put("server_id",platformPayParam.getServerID());
        paramsMap.put("server_name",platformPayParam.getServerName());
        paramsMap.put("role_id",platformPayParam.getRoleID());
        paramsMap.put("role_name",platformPayParam.getRoleName());
        paramsMap.put("sign",OrderUtil.encryptPaySign(SDKManager.getInstance().getActivity(), paramsMap));
        //platformPayParam.price=fen2yuan(platformPayParam.price) //price String ext :6.00
        //mainActivity?.showProgress("")
        String inAppProductId=    "test01" ;           //platformPayParam.productId; //todo:匹配华为的 product id
        OkGo.<LzyResponse<OrderNumberBean>>post(ORDER_CREATE)
                .tag(ORDER_CREATE)
                .params(paramsMap)
                .execute(new JsonCallback<LzyResponse<OrderNumberBean>>() {
                    @Override
                    public void onSuccess(Response<LzyResponse<OrderNumberBean>> response) {
                        if(response.body()!=null && response.body().data!=null){
                            OrderNumberBean data = response.body().data;
                            checkPayEnvAndMaybePay(inAppProductId,data.getNumber());
                        }
                    }

                    @Override
                    public void onError(String errorMsg, int code) {
                        Toast.makeText(
                        SDKManager.getInstance().getApplication(),errorMsg,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void pay(String storeProductId,String developerPayloadOrderNumber){
        checkPayEnvAndMaybePay(storeProductId,developerPayloadOrderNumber);
    }
    public void checkPayEnvAndMaybePay(String storeProductId, String developerPayloadOrderNumber){
        // 获取调用接口的Activity对象
        Activity activity = SDKManager.getInstance().getActivity();
        Task<IsEnvReadyResult> task = Iap.getIapClient(activity).isEnvReady();
        task.addOnSuccessListener(new OnSuccessListener<IsEnvReadyResult>() {
            @Override
            public void onSuccess(IsEnvReadyResult result) {
                // 获取接口请求的结果
                startPay(storeProductId,developerPayloadOrderNumber);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException) e;
                    Status status = apiException.getStatus();
                    if (status.getStatusCode() == OrderStatusCode.ORDER_HWID_NOT_LOGIN) {
                        // 未登录帐号
                        if (status.hasResolution()) {
                            try {
                                // 6666是开发者自定义的int类型常量
                                // 启动IAP返回的登录页面
                               status.startResolutionForResult(activity, REQ_CODE_LOGIN);
                            } catch (IntentSender.SendIntentException exp) {
                                //Toast.makeText(activity,"用户当前登录的华为帐号所在的服务地不在华为IAP支持结算的国家或地区",Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (status.getStatusCode() == OrderStatusCode.ORDER_ACCOUNT_AREA_NOT_SUPPORTED) {
                        // 用户当前登录的华为帐号所在的服务地不在华为IAP支持结算的国家或地区
                        Toast.makeText(activity,"用户当前登录的华为帐号所在的服务地不在华为IAP支持结算的国家或地区",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_LOGIN) {
            if (data != null) {
                // 获取接口请求结果
                int returnCode = IapClientHelper.parseRespCodeFromIntent(data);
            }
        }
        if (requestCode == REQ_CODE_BUY) {
            if (data == null) {
                Toast.makeText(SDKManager.getInstance().getActivity(), "error", Toast.LENGTH_SHORT).show();
                return;
            }
            PurchaseResultInfo purchaseResultInfo = Iap.getIapClient(
                    SDKManager.getInstance().getActivity()).parsePurchaseResultInfoFromIntent(data);
            switch(purchaseResultInfo.getReturnCode()) {
                case OrderStatusCode.ORDER_STATE_SUCCESS:
                    //consumeOwnedPurchase(SDKManager.getInstance().getActivity(), purchaseResultInfo.getInAppPurchaseData());

                    Log.e(TAG,"inapppurchasedata"+purchaseResultInfo.getInAppPurchaseData());
                    Log.e(TAG,"inapppurchasesignature"+purchaseResultInfo.getInAppDataSignature());
                    //todo:在服务端验签发货并消耗
                    try {
                        JSONObject jsonObject=new JSONObject(purchaseResultInfo.getInAppPurchaseData());
                        SDKManager.deliverProduct(jsonObject.getString("purchaseToken"),
                                jsonObject.getString("productId"),
                                jsonObject.getString("developerPayload"),
                                new JsonCallback<SimpleResponse>() {
                                    @Override
                                    public void onSuccess(Response<SimpleResponse> response) {
                                        if(response.body()!=null && response.body().code==0){
                                            consumeOwnedPurchase(SDKManager.getInstance().getActivity(),
                                                    purchaseResultInfo.getInAppPurchaseData());
                                        }
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                case OrderStatusCode.ORDER_STATE_CANCEL:
                    // The User cancels payment.
                    Toast.makeText(SDKManager.getInstance().getActivity(), "user cancel", Toast.LENGTH_SHORT).show();
                    return;
                case OrderStatusCode.ORDER_PRODUCT_OWNED:
                    // The user has already owned the product.
                    Toast.makeText(SDKManager.getInstance().getActivity(), "你有订单尚未处理，正在为您处理", Toast.LENGTH_SHORT).show();
                    // you can check if the user has purchased the product and decide whether to provide goods
                    // if the purchase is a consumable product, consuming the purchase and deliver product
                    handleOwnedProduct();
                    return;

                default:
                    Toast.makeText(SDKManager.getInstance().getActivity(), "Pay failed", Toast.LENGTH_SHORT).show();
                    break;
            }
            return;
        }

    }

    public void handleOwnedProduct() {
        // 构造一个OwnedPurchasesReq对象
        OwnedPurchasesReq ownedPurchasesReq = new OwnedPurchasesReq();
        // priceType: 0：消耗型商品; 1：非消耗型商品; 2：订阅型商品
        ownedPurchasesReq.setPriceType(0);
        // 获取调用接口的Activity对象
        Activity activity = SDKManager.getInstance().getActivity();
        // 调用obtainOwnedPurchases接口获取所有已购但未发货的消耗型商品
        Task<OwnedPurchasesResult> task = Iap.getIapClient(activity).obtainOwnedPurchases(ownedPurchasesReq);
        task.addOnSuccessListener(new OnSuccessListener<OwnedPurchasesResult>() {
            @Override
            public void onSuccess(OwnedPurchasesResult result) {
                // 获取接口请求成功的结果
                if (result != null && result.getInAppPurchaseDataList() != null) {
                    for (int i = 0; i < result.getInAppPurchaseDataList().size(); i++) {
                        String inAppPurchaseData = result.getInAppPurchaseDataList().get(i);
                        String inAppSignature = result.getInAppSignature().get(i);
                        // 使用应用的IAP公钥验证inAppPurchaseData的签名数据
                        // 如果验签成功，确认每个商品的购买状态。确认商品已支付后，检查此前是否已发过货，未发货则进行发货操作。发货成功后执行消耗操作
                        try {
                            InAppPurchaseData inAppPurchaseDataBean = new InAppPurchaseData(inAppPurchaseData);
                            int purchaseState = inAppPurchaseDataBean.getPurchaseState();
                            JSONObject jsonObject=new JSONObject(inAppPurchaseData);
                            SDKManager.deliverProduct(jsonObject.getString("purchaseToken"),
                                    jsonObject.getString("productId"),
                                    jsonObject.getString("developerPayload"),
                                    new JsonCallback<SimpleResponse>() {
                                        @Override
                                        public void onSuccess(Response<SimpleResponse> response) {
                                            if(response.body()!=null && response.body().code==0){
                                                consumeOwnedPurchase(SDKManager.getInstance().getActivity(),
                                                       inAppPurchaseData);
                                            }
                                        }
                                    });
                            Log.e(TAG,"inapppurchasedata"+inAppPurchaseData.toString());
                            //consumeOwnedPurchase(SDKManager.getInstance().getActivity(),inAppPurchaseData);
                            throw new JSONException("");
                        } catch (JSONException e) {
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException) e;
                    Status status = apiException.getStatus();
                    int returnCode = apiException.getStatusCode();
                } else {
                    // 其他外部错误
                }
            }
        });
    }

    private void startPay(String storeProductId, String developerPayloadOrderNumber) {
        // 构造一个PurchaseIntentReq对象
        PurchaseIntentReq req = new PurchaseIntentReq();
        // 通过createPurchaseIntent接口购买的商品必须是开发者在AppGallery Connect网站配置的商品。
        req.setProductId(storeProductId);
        // priceType: 0：消耗型商品; 1：非消耗型商品; 2：订阅型商品
        req.setPriceType(0);
        req.setDeveloperPayload(developerPayloadOrderNumber);
        // 获取调用接口的Activity对象
        final Activity activity = SDKManager.getInstance().getActivity();
        // 调用createPurchaseIntent接口创建托管商品订单
        Task<PurchaseIntentResult> task = Iap.getIapClient(activity).createPurchaseIntent(req);
        task.addOnSuccessListener(new OnSuccessListener<PurchaseIntentResult>() {
            @Override
            public void onSuccess(PurchaseIntentResult result) {
                Log.i(TAG, "createPurchaseIntent, onSuccess");
                if (result == null) {
                    Log.e(TAG, "result is null");
                    return;
                }
                Status status = result.getStatus();
                if (status == null) {
                    Log.e(TAG, "status is null");
                    return;
                }
                // you should pull up the page to complete the payment process.
                if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(activity, REQ_CODE_BUY);
                    } catch (IntentSender.SendIntentException exp) {
                        Log.e(TAG, exp.getMessage());
                    }
                } else {
                    Log.e(TAG, "intent is null");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //TODO
                Log.e(TAG, e.getMessage());
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException)e;
                    int returnCode = apiException.getStatusCode();
                    Log.e(TAG, "createPurchaseIntent, returnCode: " + returnCode);
                    // handle error scenarios
                } else {
                    // Other external errors
                }
            }
        });
    }

    /**
     * Consume the unconsumed purchase with type 0 after successfully delivering the product, then the Huawei payment server will update the order status and the user can purchase the product again.
     * @param inAppPurchaseData JSON string that contains purchase order details.
     */
    public void consumeOwnedPurchase(final Context context, String inAppPurchaseData) {
        Log.i(TAG, "call consumeOwnedPurchase");
        IapClient mClient = Iap.getIapClient(context);
        Task<ConsumeOwnedPurchaseResult> task = mClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData));
        task.addOnSuccessListener(new OnSuccessListener<ConsumeOwnedPurchaseResult>() {
            @Override
            public void onSuccess(ConsumeOwnedPurchaseResult result) {
                // Consume success
                Log.i(TAG, "consumeOwnedPurchase success");
                Toast.makeText(context, "Pay success, and the product has been delivered", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException)e;
                    Status status = apiException.getStatus();
                    int returnCode = apiException.getStatusCode();
                    Log.e(TAG, "consumeOwnedPurchase fail,returnCode: " + returnCode);
                } else {
                    // Other external errors
                }

            }
        });
    }

    /**
     * Create a ConsumeOwnedPurchaseReq instance.
     * @param purchaseData JSON string that contains purchase order details.
     * @return ConsumeOwnedPurchaseReq
     */
    private ConsumeOwnedPurchaseReq createConsumeOwnedPurchaseReq(String purchaseData) {

        ConsumeOwnedPurchaseReq req = new ConsumeOwnedPurchaseReq();
        // Parse purchaseToken from InAppPurchaseData in JSON format.
        try {
            InAppPurchaseData inAppPurchaseData = new InAppPurchaseData(purchaseData);
            req.setPurchaseToken(inAppPurchaseData.getPurchaseToken());
        } catch (JSONException e) {
            Log.e(TAG, "createConsumeOwnedPurchaseReq JSONExeption");
        }

        return req;
    }

    /**
     * the method to check the signature for the data returned from the interface
     * @param content Unsigned data
     * @param sign the signature for content
     * @param publicKey the public of the application
     * @return boolean
     */

    private static final String SIGN_ALGORITHMS = "SHA256WithRSA";

    public  boolean doCheck(String content, String sign, String publicKey) {
        if (TextUtils.isEmpty(publicKey)) {
            Log.e(TAG, "publicKey is null");
            return false;
        }

        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(sign)) {
            Log.e(TAG, "data is error");
            return false;
        }

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(publicKey, Base64.DEFAULT);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));

            boolean bverify = signature.verify(Base64.decode(sign, Base64.DEFAULT));
            return bverify;

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "doCheck NoSuchAlgorithmException" + e);
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "doCheck InvalidKeySpecException" + e);
        } catch (InvalidKeyException e) {
            Log.e(TAG, "doCheck InvalidKeyException" + e);
        } catch (SignatureException e) {
            Log.e(TAG, "doCheck SignatureException" + e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "doCheck UnsupportedEncodingException" + e);
        }
        return false;
    }

}
