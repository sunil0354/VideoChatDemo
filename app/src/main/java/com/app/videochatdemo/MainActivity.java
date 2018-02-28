package com.app.videochatdemo;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import com.opentok.android.AudioDeviceManager;
import com.opentok.android.BaseAudioDevice;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {
    private static String API_KEY = "46003252"; // TODO: This is your app id which you have registered in opentok
    private static String SESSION_ID = "2_MX40NjAwMzI1Mn5-MTUxOTYzMDMyNzAwOH41YTBoc29FcjFHNHpzUXJmcWRCQW9YVUN-fg"; // TODO: This is used for session where user chats with other user, this session is limited for some time
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjAwMzI1MiZzZGtfdmVyc2lvbj1kZWJ1Z2dlciZzaWc9MzllNTc0YmFmZGM2MmQ5ZDFkMjNjMTIxY2Y2ZmIwYjRkYzI1YmEyMDpzZXNzaW9uX2lkPTJfTVg0ME5qQXdNekkxTW41LU1UVXhPVFl6TURNeU56QXdPSDQxWVRCb2MyOUZjakZITkhwelVYSm1jV1JDUVc5WVZVTi1mZyZjcmVhdGVfdGltZT0xNTE5NjMwMzI3JnJvbGU9bW9kZXJhdG9yJm5vbmNlPTE1MTk2MzAzMjcuMDE4MTUzNjE0Mjg3NyZleHBpcmVfdGltZT0xNTIyMjIyMzI3";
    private static final int RC_VIDEO_APP_PERM = 124;
    private Session mSession;
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private static String TAG="tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
//            m_amAudioManager.set
            mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);
            // initialize and connect to the session
            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onConnected(Session session) {
        mPublisher = new Publisher.Builder(this).videoTrack(false).build(); // TODO: Uncomment this line if you want a Audio chat only
//        mPublisher = new Publisher.Builder(this).build(); // TODO: Uncomment this line if you want a Video chat only
        mPublisher.setPublisherListener(this);
        mPublisherViewContainer.addView(mPublisher.getView());
        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        AudioDeviceManager.getAudioDevice().setOutputMode(BaseAudioDevice.OutputMode.Handset); // TODO: This line is used to listen audio from front speaker(Ear piece), if you want to listen voice from loud speaker in case of videoo chat then Remove this line
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

}
