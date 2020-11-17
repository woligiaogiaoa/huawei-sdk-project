package com.batam.huawei;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.api.HuaweiMobileServicesUtil;
import com.huawei.hms.common.ApiException;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class SDKManager {

    private static final String TAG = SDKManager.class.getSimpleName();
    private final static int SIGN_IN_INTENT = 3000;
    private final static int HEARTBEAT_TIME = 15 * 60 * 1000;

    private String playerId;

    private String sessionId = null;

    private boolean hasInit = false;

    private Handler handler;

    private Application application;

    private Activity activity;

    private SDKManager() {
    }

    public static SDKManager getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final SDKManager sInstance = new SDKManager();
    }

    public void setApplicationContext(Application application) {
        this.application = application;
        HuaweiMobileServicesUtil.setApplication(application);
    }

    public void init(Activity activity) {
        this.activity = activity;
        JosAppsClient appsClient = JosApps.getJosAppsClient(activity);
        appsClient.init();
        showLog("init success");
        hasInit = true;
        checkUpdate();
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
        Log.e(TAG, "onResume");
    }


    public void login() {
        Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.getService(activity, getHuaweiIdParams()).silentSignIn();
        authHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
            @Override
            public void onSuccess(AuthHuaweiId authHuaweiId) {
                showLog("signIn success");
                showLog("display:" + authHuaweiId.getDisplayName());
                SignInCenter.get().updateAuthHuaweiId(authHuaweiId);
                getCurrentPlayer();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    showLog("signIn failed:" + apiException.getStatusCode());
                    showLog("start getSignInIntent");
                    signInNewWay();
                }
            }
        });
    }

    private void signInNewWay() {
        Intent intent = HuaweiIdAuthManager.getService(activity, getHuaweiIdParams()).getSignInIntent();
        activity.startActivityForResult(intent, SIGN_IN_INTENT);
    }

    private HuaweiIdAuthParams getHuaweiIdParams() {
        return new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).createParams();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SIGN_IN_INTENT == requestCode) {
            handleSignInResult(data);
        } else {
            showLog("unknown requestCode in onActivityResult");
        }
    }

    private void handleSignInResult(Intent data) {
        if (null == data) {
            showLog("signIn inetnt is null");
            return;
        }
        // HuaweiIdSignIn.getSignedInAccountFromIntent(data);
        String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
        if (TextUtils.isEmpty(jsonSignInResult)) {
            showLog("SignIn result is empty");
            return;
        }
        try {
            HuaweiIdAuthResult signInResult = new HuaweiIdAuthResult().fromJson(jsonSignInResult);
            if (0 == signInResult.getStatus().getStatusCode()) {
                showLog("Sign in success.");
                showLog("Sign in result: " + signInResult.toJson());
                SignInCenter.get().updateAuthHuaweiId(signInResult.getHuaweiId());
                getCurrentPlayer();
            } else {
                showLog("Sign in failed: " + signInResult.getStatus().getStatusCode());
            }
        } catch (JSONException var7) {
            showLog("Failed to convert json from signInResult.");
        }
    }

    private void getCurrentPlayer() {
        PlayersClientImpl client = (PlayersClientImpl) Games.getPlayersClient(activity);

        Task<Player> task = client.getCurrentPlayer();
        task.addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                String result = "display:" + player.getDisplayName() + "\n" + "playerId:" + player.getPlayerId() + "\n"
                        + "playerLevel:" + player.getLevel() + "\n" + "timestamp:" + player.getSignTs() + "\n"
                        + "playerSign:" + player.getPlayerSign();
                showLog(result);
                playerId = player.getPlayerId();
                gameBegin();
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
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    showLog(result);
                }
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
            showLog("GetCurrentPlayer first.");
            return;
        }
        String uid = UUID.randomUUID().toString();
        PlayersClient client = Games.getPlayersClient(activity);
        Task<String> task = client.submitPlayerEvent(playerId, uid, "GAMEBEGIN");
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String jsonRequest) {
                if (jsonRequest == null) {
                    showLog("jsonRequest is null");
                    return;
                }
                try {
                    JSONObject data = new JSONObject(jsonRequest);
                    sessionId = data.getString("transactionId");
                } catch (JSONException e) {
                    showLog("parse jsonArray meet json exception");
                    return;
                }
                showLog("submitPlayerEvent traceId: " + jsonRequest);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    showLog(result);
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
            showLog("GetCurrentPlayer first.");
            return;
        }
        if (TextUtils.isEmpty(sessionId)) {
            showLog("SessionId is empty.");
            return;
        }
        PlayersClient client = Games.getPlayersClient(activity);
        Task<String> task = client.submitPlayerEvent(playerId, sessionId, "GAMEEND");
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                showLog("submitPlayerEvent traceId: " + s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    showLog(result);
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
            showLog("GetCurrentPlayer first.");
            return;
        }
        PlayersClient client = Games.getPlayersClient(activity);
        Task<PlayerExtraInfo> task = client.getPlayerExtraInfo(sessionId);
        task.addOnSuccessListener(new OnSuccessListener<PlayerExtraInfo>() {
            @Override
            public void onSuccess(PlayerExtraInfo extra) {
                if (extra != null) {
                    showLog("IsRealName: " + extra.getIsRealName() + ", IsAdult: " + extra.getIsAdult() + ", PlayerId: "
                            + extra.getPlayerId() + ", PlayerDuration: " + extra.getPlayerDuration());
                } else {
                    showLog("Player extra info is empty.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    showLog(result);
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
                    sdkManager.showLog("check update success");
                    AppUpdateClient client = JosApps.getAppUpdateClient(sdkManager.activity);
                    /**
                     * show update dialog
                     * *
                     * 弹出升级提示框
                     */
                    client.showUpdateDialog(sdkManager.activity, (ApkUpgradeInfo) info, false);
                } else {
                    sdkManager.showLog("check update failed");
                }
            }
        }

        // ignored
        // 预留, 无需处理
        @Override
        public void onMarketInstallInfo(Intent intent) {
            Log.w("AppUpdateManager", "info not instanceof ApkUpgradeInfo");
            sdkManager.showLog("check update failed");
        }

        // ignored
        // 预留, 无需处理
        @Override
        public void onMarketStoreError(int responseCode) {
            sdkManager.showLog("check update failed");
        }

        // ignored
        // 预留, 无需处理
        @Override
        public void onUpdateStoreError(int responseCode) {
            sdkManager.showLog("check update failed");
        }
    }
    private void showLog(String result) {
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
    }
}