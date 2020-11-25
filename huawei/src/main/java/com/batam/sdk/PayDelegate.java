package com.batam.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.huawei.hms.iap.entity.OwnedPurchasesReq;
import com.huawei.hms.iap.entity.OwnedPurchasesResult;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseIntentResult;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.iap.util.IapClientHelper;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.log.common.Base64;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

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
                    Log.e(TAG,"inapppurchasedata"+purchaseResultInfo.getInAppPurchaseData().toString());
                    //fixme:支付成功之后，给服务器发订单接口调用成功才消耗
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

    private void handleOwnedProduct() {
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
                            //todo:验签，发货，消耗
                            consumeOwnedPurchase(SDKManager.getInstance().getActivity(), inAppPurchaseData);
                            Log.e(TAG,"inapppurchasedata"+inAppPurchaseData.toString());
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

    /**
     * 校验签名信息
     *
     * @param content 结果字符串
     * @param sign 签名字符串
     * @param publicKey 支付公钥
     * @return 是否校验通过
     */

    public static boolean doCheck(String content, String sign, String publicKey) {
        if (sign == null) {
            return false;
        }
        if (publicKey == null) {
            return false;
        }
        try {
            // 生成"RSA"的KeyFactory对象
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(publicKey);
            // 生成公钥
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = null;
            // 根据SHA256WithRSA算法获取签名对象实例
            signature = java.security.Signature.getInstance("SHA256WithRSA");
            // 初始化验证签名的公钥
            signature.initVerify(pubKey);
            // 把原始报文更新到签名对象中
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            // 将sign解码
            byte[] bsign = Base64.decode(sign);
            // 进行验签
            return signature.verify(bsign);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
