package com.batam.sdk;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.huawei.hms.support.log.common.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.batam.huawei.test", appContext.getPackageName());
    }

    @Test
    public void testSign(){
        String inAppPurchaseData="";
        String inAppPurchaseSignature="";
        Boolean validate=doCheck(inAppPurchaseData,inAppPurchaseData,getKey());
        assertEquals(validate,true);
    }

    private String getKey(){
        return "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAlhOL2g5fxK4IrfGUzSKLxG0h4iLC8DcFNiRw4jUs/s0SeZPAg0duUzyjNzEsylQkHeb991ImpJjCg6L1KGQWbBJm1YSemlKwF3SWNVvdfW3x6qQryjUaAe3LYfe0xXTrtPiVPTswZj5Yqs5SqsMchcxAm9r9Cs2E/S2S1VHmiCziLGIrwz5Dvek5xT0ODKYGaFsi62QvNIJtVDJN5fWHAz7ASg4YkVQeTv2NWe/DZBvmKexkMUozmvNUQU+mqY3NV8Jup6yF+GvBCK83LtQmrq1AYciYldxUKLCQVdQZHmXhQrhRU0Ui2LFbcPhSpOPvOsW0TfVUG0l0HsGe7n0H9H2gJfQzsOtfr5UscJ/jtviYzOKkGfJ3k/NEhMUXQTrnAEUxchTL58v2vb16RJkb3Hyv/vUtSm3dCbF5fYKW5hkq5MbsIoVG0IP8rc13akjmvVAZveUxltIqkKehVg10nletkW0C8Cx5Pw8BgpU/fEgK9PQ21yKKljUyAt3uAwRJAgMBAAE=";
    }


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