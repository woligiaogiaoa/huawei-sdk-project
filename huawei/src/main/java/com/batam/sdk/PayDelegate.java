package com.batam.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseIntentResult;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.iap.util.IapClientHelper;
import com.huawei.hms.support.api.client.Status;

import org.json.JSONException;

public class PayDelegate {

    private String TAG="PAY_DELEGATE";
    private final int REQ_CODE_BUY=10001;
    private final int REQ_CODE_LOGIN=10002;

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
            PurchaseResultInfo purchaseResultInfo = Iap.getIapClient(SDKManager.getInstance().getActivity()).parsePurchaseResultInfoFromIntent(data);
            switch(purchaseResultInfo.getReturnCode()) {
                case OrderStatusCode.ORDER_STATE_SUCCESS:
                    consumeOwnedPurchase(SDKManager.getInstance().getActivity(), purchaseResultInfo.getInAppPurchaseData());
                    //fixme:支付成功之后，给服务器发订单接口调用成功才消耗
                    return;
                case OrderStatusCode.ORDER_STATE_CANCEL:
                    // The User cancels payment.
                    Toast.makeText(SDKManager.getInstance().getActivity(), "user cancel", Toast.LENGTH_SHORT).show();
                    return;
                case OrderStatusCode.ORDER_PRODUCT_OWNED:
                    // The user has already owned the product.
                    Toast.makeText(SDKManager.getInstance().getActivity(), "you have owned the product", Toast.LENGTH_SHORT).show();
                    // you can check if the user has purchased the product and decide whether to provide goods
                    // if the purchase is a consumable product, consuming the purchase and deliver product
                    return;

                default:
                    Toast.makeText(SDKManager.getInstance().getActivity(), "Pay failed", Toast.LENGTH_SHORT).show();
                    break;
            }
            return;
        }

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
    private void consumeOwnedPurchase(final Context context, String inAppPurchaseData) {
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

}
