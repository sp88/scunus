package com.example.sahan.scunus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sahan.scunus.reedsolomon.Util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import de.frank_durr.ecdh_curve25519.ECDHCurve25519;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("ecdhcurve25519");
    }

    private Button senderView;
    private Button receiverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senderView = (Button) findViewById(R.id.sender_button);
        senderView.setOnClickListener(this);

        receiverView = (Button) findViewById(R.id.receiver_button);
        receiverView.setOnClickListener(this);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());

//        // Create secret key from a big random number.
//        SecureRandom random = new SecureRandom();
//        byte[] my_secret_key = ECDHCurve25519.generate_secret_key(random);
//        Log.e("Secret_key",  Util.toHex(my_secret_key));
//        // Create public key.
//        byte[] my_public_key = ECDHCurve25519.generate_public_key(my_secret_key);
//        Log.e("my_public_key",  Util.toHex(my_public_key));
//        Log.e("my_public_key length", ": " + new String(my_public_key).length() );
//        tv.setText(Util.toHex(my_public_key));

// Create Alice's secret key from a big random number.
        SecureRandom random = new SecureRandom();
        byte[] alice_secret_key = ECDHCurve25519.generate_secret_key(random);
// Create Alice's public key.
        byte[] alice_public_key = ECDHCurve25519.generate_public_key(alice_secret_key);

// Bob is also calculating a key pair.
        byte[] bob_secret_key = ECDHCurve25519.generate_secret_key(random);
        byte[] bob_public_key = ECDHCurve25519.generate_public_key(bob_secret_key);

// Assume that Alice and Bob have exchanged their public keys.

// Alice is calculating the shared secret.
        byte[] alice_shared_secret = ECDHCurve25519.generate_shared_secret(
                alice_secret_key, bob_public_key);
        String alice_shared_string = Util.toHex(alice_shared_secret);
        Log.e("alice_shared_secret", alice_shared_string);

// Bob is also calculating the shared secret.
        byte[] bob_shared_secret = ECDHCurve25519.generate_shared_secret(
                bob_secret_key, alice_public_key);
        String bob_shared_string = Util.toHex(bob_shared_secret);
        Log.e("bob_shared_secret", bob_shared_string);
        Log.e("Shared String Equals", ": "+ bob_shared_string.equals(alice_shared_string));


        /**
         try {
         byte[] password = "sahan perera".getBytes(StandardCharsets.UTF_8);
         //            byte[] pkey = "keykeykekeykeykekeykeykekeykeyke".getBytes(StandardCharsets.UTF_8);
         byte[] pkey = bob_shared_string.getBytes(StandardCharsets.UTF_8);
         SecretKeySpec secretKey = new SecretKeySpec(pkey, "AES");
         Cipher c = Cipher.getInstance("AES/OCB");
         Log.e("User password: " , new String(password));

         // encrypt password
         byte[] cText = new byte[password.length];
         c.init(Cipher.ENCRYPT_MODE, secretKey);
         int ctLen = c.update(password, 0, password.length, cText, 0);
         ctLen += c.doFinal(cText, ctLen);
         Log.e("Password encrypted: ", Util.toHex(cText) + " bytes: " + ctLen);


         // decrypt password
         byte[] plainText = new byte[ctLen];
         c.init(Cipher.DECRYPT_MODE, secretKey);
         int plen = c.update(cText, 0, ctLen, plainText, 0);
         plen += c.doFinal(plainText, plen);
         Log.e("User password : ", new String(plainText) + " bytes: " + plen);
         } catch (InvalidKeyException | ShortBufferException | IllegalBlockSizeException
         | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e){
         Log.e("Encrypt/Decrypt", e.getMessage());
         }
         /**  **/

        // Original text
        String theTestText = "Name";

        // Set up secret key spec for 256-bit AES encryption and decryption
        SecretKeySpec sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(bob_shared_string.getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
//            kg.init(128, sr);
            kg.init(256, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("Set up Secret", "AES secret key spec error");
        }

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(theTestText.getBytes());
            Log.e("Encode", Util.toHex(encodedBytes));
        } catch (Exception e) {
            Log.e("Encode Exception", "AES encryption error");
        }
//        TextView tvencoded = (TextView) findViewById(R.id.tvencoded);
//        tvencoded.setText("[ENCODED]:\n" +
//                Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");

        // Decode the encoded data with AES
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(encodedBytes);
            Log.e("Decode", new String(decodedBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e("Decode Exception", "AES decryption error");
        }

    }

    public String getSharedSecret(byte[] mySecretKey, byte[] otherPublicKey){
        byte[] shared_secret = ECDHCurve25519.generate_shared_secret(
                mySecretKey, otherPublicKey);
        String ss = Util.toHex(shared_secret);
        Log.e("Shared Secret", ss);
        return ss;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.receiver_button){
            Intent i = new Intent(this, ReceiverActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, SenderActivity.class);
            startActivity(i);
        }
    }
}
