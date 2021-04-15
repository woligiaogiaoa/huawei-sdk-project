package com.batam.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.batam.sdk.data.data.DeviceUtils;
import com.batam.sdk.data.login.SlugBean;
import com.batam.sdk.data.pay.HuaweiPayParam;
import com.batam.sdk.http.JsonCallback;
import com.batam.sdk.http.LzyResponse;
import com.batam.sdk.http.SimpleResponse;
import com.batam.sdk.util.SignInCenter;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.api.HuaweiMobileServicesUtil;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.hms.iap.entity.ProductInfoReq;
import com.huawei.hms.iap.entity.ProductInfoResult;
import com.huawei.hms.jos.AppUpdateClient;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.jos.games.player.PlayerExtraInfo;
import com.huawei.hms.jos.games.player.PlayersClientImpl;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;
import com.huawei.updatesdk.service.appmgr.bean.ApkUpgradeInfo;
import com.huawei.updatesdk.service.otaupdate.CheckUpdateCallBack;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class SDKManager {

    private static final String TAG = SDKManager.class.getSimpleName();
    private final static int SIGN_IN_INTENT = 3000;
    private final static int HEARTBEAT_TIME = /*BuildConfig.DEBUG? 5000 :*/11 * 60 * 1000; //测试环境下5秒


    private String playerId; //use openid

    private String slug; //是否登录到后端

    public PayDelegate payDelegate;

    private String sessionId = null;

    private boolean hasInit = false;

    private Userlistener userlistener;

    private Handler handler;

    private Application application;

    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private SDKManager() {
    }

    private void setPlayId(String openId){
        playerId=openId;
    }

    public static SDKManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Userlistener getUserlistener() {
        return userlistener;
    }

    public void setUserlistener(Userlistener userlistener) {
        this.userlistener = userlistener;
    }

    public void consume() {
        payDelegate.handleOwnedProduct();
    }

    private static class SingletonHolder {
        private static final SDKManager sInstance = new SDKManager();
    }

    public void setApplicationContext(Application application) {
        this.application = application;
        HuaweiMobileServicesUtil.setApplication(application); //huawei api
        DeviceUtils.setApp(application);
    }

    public void init(Activity activity) {
        this.activity = activity;
        payDelegate=new PayDelegate();
        payDelegate.handleOwnedProduct();
        JosAppsClient appsClient = JosApps.getJosAppsClient(activity);
        appsClient.init();
        //showToastIfdDebug("init success");
        hasInit = true;
        checkUpdate();
        loadProducts();

     }

    private void loadProducts() {
        //load id lists , then load products
    }

    //fixme:获取商品id列表
    List<String> productIdList = new ArrayList<>();

    List<ProductInfo> productList=new ArrayList<>();

    private void getProductsByIds() {

// 查询的商品必须是开发者在AppGallery Connect网站配置的商品
        ProductInfoReq req = new ProductInfoReq();
// priceType: 0：消耗型商品; 1：非消耗型商品; 2：订阅型商品
        req.setPriceType(0);
        req.setProductIds(productIdList);
// 获取调用接口的Activity对象
        Activity activity = getActivity();
// 调用obtainProductInfo接口获取AppGallery Connect网站配置的商品的详情信息
        Task<ProductInfoResult> task = Iap.getIapClient(activity).obtainProductInfo(req);
        task.addOnSuccessListener(new OnSuccessListener<ProductInfoResult>() {
            @Override
            public void onSuccess(ProductInfoResult result) {
                // 获取接口请求成功时返回的商品详情信息
                productList = result.getProductInfoList();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException) e;
                    int returnCode = apiException.getStatusCode();
                } else {
                    // 其他外部错误
                }
            }
        });
    }

    public void onStop() {
        gameEnd();
        Log.e(TAG, "onStop");
    }

    public void onStart() {
        gameBegin();
        Log.e(TAG, "onStart");
    }

    public void onPause() {
        hideFloatWindowNewWay();
    }

    public void onResume() {
        showFloatWindowNewWay();
        //fixme:check player
       // checkPlayer();
        Log.e(TAG, "onResume");
    }

    private void checkPlayer() {
        if(TextUtils.isEmpty(playerId)) return;
        PlayersClientImpl client = (PlayersClientImpl) Games.getPlayersClient(activity);

        Task<Player> task = client.getGamePlayer(true);
        task.addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                String result = "display:" + player.getDisplayName() + "\n" + "playerId:" + player.getPlayerId() + "\n"
                        + "playerLevel:" + player.getLevel() + "\n" + "timestamp:" + player.getSignTs() + "\n"
                        + "playerSign:" + player.getPlayerSign()
                        ;
                //showToastIfdDebug(result);
                String playerWhenResume = player.getOpenId();
                Log.e(TAG, "resume openid:"+player.getOpenId() +"\n"+"original playerid:" +playerId );
                if(!TextUtils.isEmpty(playerId)){
                    if(!playerId.equals(playerWhenResume)){
                        if(userlistener!=null){
                            logout();
                            userlistener.onUserSwitchAccount(); //
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "checkplayer fail rtnCode:" + ((ApiException) e).getStatusCode();
                    showToastIfdDebug(result);
                }
            }
        });
    }

    public void onDestroy() {
        Log.e(TAG, "onDestroy");
    }

    public void logout(){
        gameEnd();
        playerId=null; //sdk逻辑上的退出，不用管华为的。强制重新登陆。
        userlistener.onLogout();
        Task<Void> huaweiLogoutTask = HuaweiIdAuthManager.getService(activity, getHuaweiIdParams()).signOut();
        huaweiLogoutTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                // Processing after the sign-out.
                Log.i(TAG, "signOut complete");
            }
        });
    }


    public void login(Context context) {
        Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.getService(activity, getHuaweiIdParams()).silentSignIn();
        authHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
            @Override
            public void onSuccess(AuthHuaweiId authHuaweiId) {
                //showToastIfdDebug("signIn success");
                //showToastIfdDebug("display:" + authHuaweiId.getDisplayName());
                SignInCenter.get().updateAuthHuaweiId(authHuaweiId);
                getCurrentPlayerServerLogin();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    //showToastIfdDebug("signIn failed:" + apiException.getStatusCode());
                    showToastIfdDebug("start silentSignIn getSignInIntent");
                    signInNewWay();
                }
                else {
                   userlistener.onLoginError(e.getMessage());
                }
            }
        });
    }

    public void h5OrderJsonPay(String json){
        payDelegate.h5OrderJsonPay(json);

    }
    public void paramsPay(HuaweiPayParam huaweiPayParam){
        payDelegate.paramsPay(huaweiPayParam);
    }

    public void pay(String productId,String payloadOrderNumber ){ //支付，准备加入双击处理
        payDelegate.pay(productId,payloadOrderNumber);
    }


    private void signInNewWay() {
        Intent intent = HuaweiIdAuthManager.getService(activity, getHuaweiIdParams()).getSignInIntent();
        activity.startActivityForResult(intent, SIGN_IN_INTENT);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SIGN_IN_INTENT == requestCode) {
            handleSignInResult(data);
        }
        payDelegate.onActivityResult(requestCode,resultCode,data);
    }

    private void handleSignInResult(Intent data) {
        if (null == data) {
            showToastIfdDebug("signIn inetnt is null");
            userlistener.onLoginError("SignIn result is null");
            return;
        }
        // HuaweiIdSignIn.getSignedInAccountFromIntent(data);
        String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
        if (TextUtils.isEmpty(jsonSignInResult)) {
            showToastIfdDebug("SignIn result is empty");
            userlistener.onLoginError("SignIn result is empty");
            return;
        }
        try {
            HuaweiIdAuthResult signInResult = new HuaweiIdAuthResult().fromJson(jsonSignInResult);
            if (0 == signInResult.getStatus().getStatusCode()) {
                //showToastIfdDebug("Sign in success.");
                showToastIfdDebug("Sign in result: " + signInResult.toJson());
                SignInCenter.get().updateAuthHuaweiId(signInResult.getHuaweiId());
                login(activity);
            } else {
                userlistener.onLoginError("Sign in failed: " + signInResult.getStatus().getStatusCode());
                showToastIfdDebug("Sign in failed: " + signInResult.getStatus().getStatusCode());
            }
        } catch (JSONException var7) {
            userlistener.onLoginError("Sign in failed: " + "Failed to convert json from signInResult.");
            showToastIfdDebug("Failed to convert json from signInResult.");
        }
    }

    private void getCurrentPlayerServerLogin() { //后端登录
        PlayersClientImpl client = (PlayersClientImpl) Games.getPlayersClient(activity);

        Task<Player> task = client.getGamePlayer(true);
        task.addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                String result = "playerId:" + player.getPlayerId() + "\n"
                        + "playerLevel:" + player.getLevel() + "\n" + "timestamp:" + player.getSignTs() + "\n"
                        + "playerSign:" + player.getPlayerSign();
                //showToastIfdDebug(result);
                Log.e("gamePlayersign:",
                        player.getPlayerSign().length()+"##"+player.getPlayerSign());
                        Log.e("gamePlayer",  "displayname:" + player.getDisplayName() + "\n"
                        +"uninonId:" + player.getUnionId() + "\n"+
                        "openid:" + player.getOpenId() + "\n"+
                        "accesstoken:" + player.getAccessToken() + "\n" +result);
                playerId=player.getPlayerId();
                //todo：should  called after server login
                serverLogin(player.getAccessToken(), player.getOpenId(), new JsonCallback<LzyResponse<SlugBean>>() {
                    @Override
                    public void onSuccess(Response<LzyResponse<SlugBean>> response) {
                        if(response.body()!=null & response.body().data!=null){
                            SlugBean data = response.body().data;
                            userlistener.onLoginSuccess(data.getSlug());
                        }
                        else {
                            userlistener.onLoginError("server error :empty user.");
                        }
                    }

                    @Override
                    public void onError(String errorMsg, int code) {
                        super.onError(errorMsg, code);
                        userlistener.onLoginError(errorMsg);
                    }
                });
                //userlistener.onLoginSuccess(playerId);
                gameBegin();
                //todo :server login enter game
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        gamePlayExtra();
                    }
                };
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        handler.sendMessage(message);
                    }
                }, HEARTBEAT_TIME, HEARTBEAT_TIME);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "getcurrent player and server login rtnCode:" + ((ApiException) e).getStatusCode();
                    showToastIfdDebug(result);
                }
                userlistener.onLoginError(e.getMessage());
            }
        });
    }

    /**
     * Enter the game,report the player's behavior when entering the game.
     * *
     * 进入游戏，上报玩家进入游戏时的行为事件。
     */
    public void gameBegin() {
        if (TextUtils.isEmpty(playerId)) {
            Log.e(TAG, "gameBegin: begine not login");
            showToastIfdDebug("game begin event error: not login.");
            return;
        }
        String uid = UUID.randomUUID().toString();
        PlayersClient client = Games.getPlayersClient(activity);
        Task<String> task = client.submitPlayerEvent(playerId, uid, "GAMEBEGIN");
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String jsonRequest) {
                if (jsonRequest == null) {
                    showToastIfdDebug("game begin event error: jsonRequest is null");
                    return;
                }
                try {
                    JSONObject data = new JSONObject(jsonRequest);
                    sessionId = data.getString("transactionId");
                    showToastIfdDebug("game begin event OKK session id is:"+ sessionId);
                } catch (JSONException e) {
                    showToastIfdDebug("game begin event error:+parse jsonArray meet json exception");
                    return;
                }
                Log.e(TAG, "game begin event: submitPlayerEvent traceId: " + jsonRequest);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "game begin event error: rtnCode:" + ((ApiException) e).getStatusCode();
                    //todo deal with code
                    showToastIfdDebug(result);
                }
            }
        });
    }

    /**
     * Quit the game, report the behavior event when the player quit the game.
     * *
     * 退出游戏，上报玩家退出游戏时的行为事件。
     */
    public void gameEnd() {
        if (TextUtils.isEmpty(playerId)) {
            showToastIfdDebug("game end event error: not login.");
            return;
        }
        if (TextUtils.isEmpty(sessionId)) {
            showToastIfdDebug("game end event error: SessionId is empty.");
            return;
        }
        PlayersClient client = Games.getPlayersClient(activity);
        Task<String> task = client.submitPlayerEvent(playerId, sessionId, "GAMEEND");
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                showToastIfdDebug("game end event okk :submitPlayerEvent traceId: " + s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    showToastIfdDebug(result);
                }
            }
        });
    }

    /**
     * Get additional player information.
     * *
     * 获取玩家附加信息。
     */
    public void gamePlayExtra() {
        if (TextUtils.isEmpty(playerId)) {
            showToastIfdDebug("query gamePlayExtra Error: not login .");
            return;
        }
        PlayersClient client = Games.getPlayersClient(activity);
        Task<PlayerExtraInfo> task = client.getPlayerExtraInfo(sessionId);
        task.addOnSuccessListener(new OnSuccessListener<PlayerExtraInfo>() {
            @Override
            public void onSuccess(PlayerExtraInfo extra) {
                if (extra != null) {
                    showToastIfdDebug("IsRealName: " + extra.getIsRealName() + ", IsAdult: " + extra.getIsAdult() + ", PlayerId: "
                            + extra.getPlayerId() + ", PlayerDuration: " + extra.getPlayerDuration());
                } else {
                    showToastIfdDebug("query gamePlayExtra Error: Player extra info is empty.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "gamePlayExtra error:rtnCode:" + ((ApiException) e).getStatusCode();
                    showToastIfdDebug(result);
                }
            }
        });
    }

    /**
     * Show the game buoy.
     * *
     * 显示游戏浮标。
     */
    private void showFloatWindowNewWay() {
        if (!hasInit) {
            init(activity);
        }
        Games.getBuoyClient(activity).showFloatWindow();
    }

    /**
     * Hide the displayed game buoy.
     * *
     * 隐藏已经显示的游戏浮标。
     */
    private void hideFloatWindowNewWay() {
        Games.getBuoyClient(activity).hideFloatWindow();
    }

    /**
     * Games released in the Chinese mainland: The update API provided by Huawei must be called upon game launch.
     * Games released outside the Chinese mainland: It is optional for calling the update API provided by Huawei upon
     * game launch.
     * *
     * 检测应用新版本，中国大陆发布的应用：应用启动时必须使用华为升级接口进行应用升级。
     * 中国大陆以外发布的应用：不强制要求。
     */
    public void checkUpdate() {
        AppUpdateClient client = JosApps.getAppUpdateClient(activity);
        client.checkAppUpdate(activity, new UpdateCallBack(this));
    }

    private static class UpdateCallBack implements CheckUpdateCallBack {
        private SDKManager sdkManager;

        private UpdateCallBack(SDKManager sdkManager) {
            this.sdkManager = sdkManager;
        }

        /**
         * Get update info from appmarket
         * *
         * 从应用市场获取的更新状态信息
         *
         * @param intent see detail:
         *               https://developer.huawei.com/consumer/cn/doc/development/HMS-References/appupdateclient#intent
         */
        @Override
        public void onUpdateInfo(Intent intent) {
            if (intent != null) {
                Serializable info = intent.getSerializableExtra("updatesdk_update_info");
                if (info instanceof ApkUpgradeInfo) {
                    //sdkManager.showToastIfdDebug("check update success");
                    AppUpdateClient client = JosApps.getAppUpdateClient(sdkManager.activity);
                    /**
                     * show update dialog
                     * *
                     * 弹出升级提示框
                     */
                    client.showUpdateDialog(sdkManager.activity, (ApkUpgradeInfo) info, false);
                } else {
                    //sdkManager.showToastIfdDebug("check update failed");
                }
            }
        }

        // ignored
        // 预留, 无需处理
        @Override
        public void onMarketInstallInfo(Intent intent) {
            Log.w("AppUpdateManager", "info not instanceof ApkUpgradeInfo");
            sdkManager.showToastIfdDebug("check update failed");
        }

        // ignored
        // 预留, 无需处理
        @Override
        public void onMarketStoreError(int responseCode) {
            sdkManager.showToastIfdDebug("check update failed");
        }

        // ignored
        // 预留, 无需处理
        @Override
        public void onUpdateStoreError(int responseCode) {
            sdkManager.showToastIfdDebug("check update failed");
        }
    }

    //show toast if debug
    private void showToastIfdDebug(String result) {
        if(BuildConfig.DEBUG)
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
    }

    private HuaweiIdAuthParams getHuaweiIdParams() {
        return new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).createParams(); //todo: 待确认：需不需要setIdToken()
    }


    /*--------------------back end functions---------------------------*/
    private static String baseUrl="https://api.xinglaogame.com/";

    private static String loginApi=baseUrl+"publisher/sdk/v1/huawei/user";
                                             ///
    private static String deliverApi=baseUrl+"publisher/sdk/v1/order/huawei/successful";

    public static final String ORDER_CREATE = baseUrl+"publisher/sdk/v1/order";

    private static void serverLogin(String huaweiToken,String openId, JsonCallback<LzyResponse<SlugBean>> callback){
        OkGo.<LzyResponse<SlugBean>> post(loginApi)
                .tag(loginApi)
                .params("accesstoken",huaweiToken)
                .params("openid",openId)
                .execute(callback);
    }

    public static void deliverProduct(String purchaseToken,String productId, String orderNumber,JsonCallback<SimpleResponse> callback){
        OkGo.<SimpleResponse> post(deliverApi)
                .tag(deliverApi)
                .params("purchaseToken",purchaseToken)
                .params("productId",productId)
                .params("order_no",orderNumber)
                .execute(callback);
    }

}