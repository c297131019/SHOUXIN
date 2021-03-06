
package com.yuntongxun.ecdemo.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.ECContentObservers;
import com.yuntongxun.ecdemo.common.ExceptionHandler;
import com.yuntongxun.ecdemo.common.base.CCPCustomViewPager;
import com.yuntongxun.ecdemo.common.dialog.ECAlertDialog;
import com.yuntongxun.ecdemo.common.dialog.ECProgressDialog;
import com.yuntongxun.ecdemo.common.utils.CrashHandler;
import com.yuntongxun.ecdemo.common.utils.DateUtil;
import com.yuntongxun.ecdemo.common.utils.DemoUtils;
import com.yuntongxun.ecdemo.common.utils.ECNotificationManager;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.common.utils.SharedPreferencesUtils;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.core.ContactsCache;
import com.yuntongxun.ecdemo.net.UpdateDialog;
import com.yuntongxun.ecdemo.pojo.Friend;
import com.yuntongxun.ecdemo.pojo.FsRobot;
import com.yuntongxun.ecdemo.service.YuntxNotifyReceiver;
import com.yuntongxun.ecdemo.storage.ContactSqlManager;
import com.yuntongxun.ecdemo.storage.ConversationSqlManager;
import com.yuntongxun.ecdemo.storage.FriendMessageSqlManager;
import com.yuntongxun.ecdemo.storage.IMessageSqlManager;
import com.yuntongxun.ecdemo.ui.adapter.MainViewPagerAdapter;
import com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper;
import com.yuntongxun.ecdemo.ui.contact.ECContacts;
import com.yuntongxun.ecdemo.ui.personcenter.PersonInfoUI;
import com.yuntongxun.ecdemo.ui.phonemodel.HttpMethods;
import com.yuntongxun.ecdemo.ui.phonemodel.PhoneUI;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECClientService;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InvalidClassException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static android.util.Log.d;

/**
 * Created by zlk on 2017/7/24.
 */

public class MainAct extends BaseActivity implements ConversationListFragment.OnUpdateMsgUnreadCountsListener {
    @BindView(R.id.vp)
    CCPCustomViewPager vp;
    @BindView(R.id.btn_tab_msg)
    TextView btnTabMsg;
    @BindView(R.id.tv_unread_msg_number)
    TextView tvUnreadMsgNumber;
    @BindView(R.id.btn_address_list)
    TextView btnAddressList;
    @BindView(R.id.tv_unread_address_number)
    TextView tvUnreadAddressNumber;
    //    @BindView(R.id.btn_workbench)
//    TextView btnWorkbench;
    @BindView(R.id.btn_my)
    TextView btnMy;
    @BindView(R.id.main_bottom)
    LinearLayout mainBottom;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    private int index;
    private ArrayList<Fragment> fragmentList;
    public MainViewPagerAdapter mMainViewPagerAdapter;
    private boolean mInitActionFlag;
    ECAlertDialog showUpdaterTipsDialog = null;
    UpdateDialog updateDialog;
    Uri updateUri;

    /**
     * 当前ECLauncherUI 实例
     */
    public static MainAct mainUI;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mainUI = this;
        updateDialog = new UpdateDialog(this);

        getPersonInfo(CCPAppManager.getUserId());
        addAI();
        initFragments();

        vp.setSlideEnabled(true);
        vp.setOffscreenPageLimit(4);
        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(),
                fragmentList);
        vp.setAdapter(mMainViewPagerAdapter);
        ECContentObservers.getInstance().initContentObserver();

        Intent intentGroup = new Intent();
        intentGroup.setAction("com.yuntongxun.ecdemo.inited");

        btnTabMsg.setSelected(true);
        sendBroadcast(intentGroup);
    }

    public static final String AI = "sx10000";

    private void addAI() {
        if (AI.equals(CCPAppManager.getUserId())) {
            return;
        }
        Friend friend = FriendMessageSqlManager.queryFriendById(AI);
        if (friend == null || !friend.isFriendLy()) {
            requestAddFriend(AI);
        }
    }

    private void initFragments() {
        fragmentList = new ArrayList<Fragment>();

        ConversationListFragment homeFra = ConversationListFragment.newInstance();//消息
        fragmentList.add(homeFra);
        MobileContactFragment dialFra = MobileContactFragment.newInstance();//通讯录
        fragmentList.add(dialFra);
//        WorkbenchFragment workbenchFragment = WorkbenchFragment.newInstance();//工作台
//        fragmentList.add(workbenchFragment);
        MyFrament myFrament = MyFrament.newInstance();//我的
        fragmentList.add(myFrament);

    }

    public void onNetWorkNotify(ECDevice.ECConnectState connect) {
        LogUtil.d(TAG, "onNetWorkNotify");
        if (vp != null && vp.getCurrentItem() == 0) {
            ConversationListFragment fragment = (ConversationListFragment) mMainViewPagerAdapter.getItem(0);
            if (fragment != null) {
                fragment.updateConnectState();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        getRobot();

//        getUserVerify();
        getDis();

        ECDevice.ECConnectState connect = SDKCoreHelper.getConnectState();
        onNetWorkNotify(connect);

        Intent intentGroup = new Intent();
        intentGroup.setAction("com.yuntongxun.ecdemo.inited");
        sendBroadcast(intentGroup);

        CrashHandler.getInstance().setContext(this);
        boolean fullExit = ECPreferences.getSharedPreferences().getBoolean(
                ECPreferenceSettings.SETTINGS_FULLY_EXIT.getId(), false);

        LogUtil.e("MainAct onresume ", "fullexit  = " + fullExit);
        if (fullExit) {
            try {
                ECPreferences.savePreference(
                        ECPreferenceSettings.SETTINGS_FULLY_EXIT, false, true);
                ContactsCache.getInstance().stop();
                CCPAppManager.setClientUser(null);
                ECDevice.unInitial();
                finish();

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

                return;
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
        }

        String account = getAutoRegistAccount();

        if (TextUtils.isEmpty(account)) {
            if (RestServerDefines.QR_APK) {
                startActivity(new Intent(this, PhoneUI.class));
            } else {
                startActivity(new Intent(this, com.yuntongxun.ecdemo.ui.activity.LoginActivity.class));
            }
            finish();
            return;
        }


        // 注册第一次登陆同步消息
        registerReceiver(new String[]{
                IMChattingHelper.INTENT_ACTION_SYNC_MESSAGE,
                SDKCoreHelper.ACTION_SDK_CONNECT});
        ClientUser user = new ClientUser("").from(account);

        ClientUser c = CCPAppManager.getClientUser();
        if (c != null) {
            user.setpVersion(c.getpVersion());
            ECApplication.version = c.getpVersion();
        }
        CCPAppManager.setClientUser(user);
        if (!ContactSqlManager.hasContact(user.getUserId())) {
            ECContacts contacts = new ECContacts();
            contacts.setClientUser(user);
            ContactSqlManager.insertContact(contacts);
        }

        if (SDKCoreHelper.getConnectState() != ECDevice.ECConnectState.CONNECT_SUCCESS
                && !SDKCoreHelper.isKickOff()) {

            ContactsCache.getInstance().load();

            if (!TextUtils.isEmpty(getAutoRegistAccount())) {
                SDKCoreHelper.init(this);
            }
        }

        OnUpdateMsgUnreadCounts();
        getTopContacts();
        if(updateUri!=null){
            installApk(updateUri);
        }else{
            updateApk();
        }
    }

    private void updateApk() {
        new PgyUpdateManager.Builder()
                .setForced(true)                //设置是否强制更新,非自定义回调更新接口此方法有用
                .setUserCanRetry(true)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
                .setDeleteHistroyApk(false)     // 检查更新前是否删除本地历史 Apk， 默认为true
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //没有更新是回调此方法
                        d("pgyer", "there is no new version");
                        File apk = new File(MainAct.this.getExternalCacheDir()+"/update.apk");
                        if(apk.exists()){
                            apk.delete();
                        }
                    }

                    @Override
                    public void onUpdateAvailable(AppBean appBean) {
                        //有更新回调此方法
                        d("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());
                        //调用以下方法，DownloadFileListener 才有效；
                        //如果完全使用自己的下载方法，不需要设置DownloadFileListener
                        if(updateDialog!=null && !updateDialog.isShowing()){
                            updateDialog.show();
                        }
                        PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        //更新拒绝（应用被下架，过期，不在安装有效期，下载次数用尽）以及无网络情况会调用此接口
                        Log.e("pgyer", "check update failed ", e);
                    }
                }).setDownloadFileListener(new DownloadFileListener() {
            @Override
            public void downloadFailed() {

            }

            @Override
            public void downloadSuccessful(Uri uri) {
                updateUri = uri;
                installApk(uri);
            }

            @Override
            public void onProgressUpdate(Integer... integers) { }
        }).register();
    }

    /**
     * 安装APK
     */
    private void installApk(Uri uri) {
        if (uri != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //兼容7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                if (this.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    startActivityForResult(intent,9527);
                }
            } catch (Throwable e) {
                ToastUtil.showMessage("解析包错误，请及时反馈");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void getUserVerify() {

        final Observer<Object> subscriber = new Observer<Object>() {
            @Override
            public void onComplete() {
                LogUtil.e("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Object movieEntity) {
                if (movieEntity != null) {
                    ResponseBody body = (ResponseBody) movieEntity;
                    try {
                        String s = new String(body.bytes());
                        LogUtil.e("getuserverify", s);
                        if (DemoUtils.isTrue(s)) {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.has("addVerify")) {
                                String v = jsonObject.getString("addVerify");
                                SharedPreferencesUtils.setParam(MainAct.this, SharedPreferencesUtils.FRIEND_TAG, v);
                            }
                        } else {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        String time = DateUtil.formatNowDate(new Date());
        String url = getSig(time);
        JSONObject map = HttpMethods.buildGetVerify(CCPAppManager.getUserId());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), map.toString());
        HttpMethods.getInstance(time).getVerify(subscriber, RestServerDefines.APPKER, url, body);


    }

    private void getRobot() {

        final Observer<Object> subscriber = new Observer<Object>() {
            @Override
            public void onComplete() {
                LogUtil.e("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Object movieEntity) {
                if (movieEntity != null) {
                    ResponseBody body = (ResponseBody) movieEntity;
                    try {
                        String s = new String(body.bytes());
                        LogUtil.e("getrobot", s);
                        if (DemoUtils.isTrue(s)) {

                            FsRobot robot = new FsRobot();
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.has("robotList")) {
                                JSONArray v = jsonObject.getJSONArray("robotList");
                                JSONObject obj = (JSONObject) v.get(0);
                                RestServerDefines.ROBOT = obj.getString("robotId");

                                if (obj.has("age")) {
                                    robot.age = obj.getString("age");
                                }
                                if (obj.has("sex")) {
                                    robot.sex = obj.getString("sex");
                                }
                                if (obj.has("name")) {
                                    robot.name = obj.getString("name");
                                }
                                CCPAppManager.setRobot(robot);
                            }

                        } else {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        String time = DateUtil.formatNowDate(new Date());
        String url = getSig(time);
        JSONObject map = HttpMethods.GetRobots();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), map.toString());
        HttpMethods.getInstance(time).GetRobots(subscriber, RestServerDefines.APPKER, url, body);


    }

    private void getDis() {

        final Observer<Object> subscriber = new Observer<Object>() {
            @Override
            public void onComplete() {
                LogUtil.e("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Object movieEntity) {
                if (movieEntity != null) {
                    ResponseBody body = (ResponseBody) movieEntity;
                    try {
                        String s = new String(body.bytes());
                        LogUtil.e("getDis", s);
                        if (DemoUtils.isTrue(s)) {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.has("result")) {
                                JSONArray v = jsonObject.getJSONArray("result");
                                if (v != null && v.length() > 0) {
                                    for (int i = 0; i < v.length(); i++) {
                                        phoneList.add(v.getString(i));

                                    }
                                }
                            }
                        } else {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        String time = DateUtil.formatNowDate(new Date());
        String url = getSig(time);
        JSONObject map = HttpMethods.buildGetDis();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), map.toString());
        HttpMethods.getInstance(time).getDisturb(subscriber, RestServerDefines.APPKER, url, body);


    }

    public static HashSet<String> phoneList = new HashSet<>();

    public static String getMessageDigest(byte[] input) {
        char[] source = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(input);
            byte[] digest = mDigest.digest();
            int length = digest.length;
            char[] result = new char[length * 2];
            int j = 0;
            for (byte l : digest) {
                result[(j++)] = source[(l >>> 4 & 0xF)];
                result[(j++)] = source[(l & 0xF)];
            }
            return new String(result);
        } catch (Exception e) {
        }
        return null;
    }

    public String getSig(String stime) {
        String s = RestServerDefines.APPKER + CCPAppManager.getAppToken() + stime;
        return getMessageDigest(s.getBytes());
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        onNetWorkNotify(SDKCoreHelper.getConnectState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getTopContacts() {

        final ArrayList<String> arrayList = ConversationSqlManager.getInstance().qureyAllSession();
        ECChatManager chatManager = ECDevice.getECChatManager();
        if (chatManager == null) {
            return;
        }
        chatManager.getSessionsOfTop(new ECChatManager.OnGetSessionsOfTopListener() {
            @Override
            public void onGetSessionsOfTopResult(ECError error, String[] sessionsArr) {
                if (error.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
                    for (String item : sessionsArr) {
                        ConversationSqlManager.updateSessionToTop(item, true);
                    }
                    List<String> list = Arrays.asList(sessionsArr);
                    for (String a : arrayList) {
                        if (!list.contains(a)) {
                            ConversationSqlManager.updateSessionToTop(a, false);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    public void onBackPressed() {
        stopService(new Intent(this,ECClientService.class));
        stopService(new Intent(this,ECClientService.InnerService.class));
        stopService(new Intent(this,YuntxNotifyReceiver.NotifyService.class));
        System.exit(0);
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(getPackageName());
//        moveTaskToBack(true);
    }

    @Override
    protected void initWidgetAciotns() {

        vp.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                reSetButton();
                switch (position) {
                    case 0:
                        btnTabMsg.setSelected(true);
                        break;
                    case 1:
                        btnAddressList.setSelected(true);
                        break;
//                    case 2:
//                        btnWorkbench.setSelected(true);
//                        break;
                    case 2:
                        btnMy.setSelected(true);
                        break;
                }
            }
        });
        // 如果是登陆过来的
        doInitAction();
    }


    @OnClick({R.id.btn_tab_msg, R.id.btn_address_list,
//            R.id.btn_workbench,
            R.id.btn_my})
    public void onViewClicked(View view) {
        reSetButton();
        switch (view.getId()) {
            case R.id.btn_tab_msg:
                index = 0;
                btnTabMsg.setSelected(true);
                break;
            case R.id.btn_address_list:
                index = 1;
                btnAddressList.setSelected(true);
                break;
//            case R.id.btn_workbench:
//                index = 2;
//                btnWorkbench.setSelected(true);
//                break;
            case R.id.btn_my:
                index = 3;
                btnMy.setSelected(true);
                break;
        }
        vp.setCurrentItem(index, true);
    }

    private void reSetButton() {
        btnTabMsg.setSelected(false);
        btnAddressList.setSelected(false);
//        btnWorkbench.setSelected(false);
        btnMy.setSelected(false);
    }

    /**
     * 处理一些初始化操作
     */
    private void doInitAction() {
        if (SDKCoreHelper.getConnectState() == ECDevice.ECConnectState.CONNECT_SUCCESS
                && !mInitActionFlag) {

            // 检测当前的版本
            SDKCoreHelper.SoftUpdate mSoftUpdate = SDKCoreHelper.mSoftUpdate;
            if (mSoftUpdate != null) {
                if (DemoUtils.checkUpdater(mSoftUpdate.version)) {
                    boolean force = mSoftUpdate.force;
                    showUpdaterTips(mSoftUpdate.desc, force);
                    if (force) {
                        return;
                    }
                }
            }

            String account = getAutoRegistAccount();
            if (!TextUtils.isEmpty(account)) {
                ClientUser user = new ClientUser("").from(account);
                CCPAppManager.setClientUser(user);
            }
            IMChattingHelper.getInstance().getPersonInfo();
            // 检测离线消息
            checkOffineMessage();
            mInitActionFlag = true;
        }
    }

    /**
     * 检查是否需要自动登录
     *
     * @return
     */
    private String getAutoRegistAccount() {
        SharedPreferences sharedPreferences = ECPreferences
                .getSharedPreferences();
        ECPreferenceSettings registAuto = ECPreferenceSettings.SETTINGS_REGIST_AUTO;
        String registAccount = sharedPreferences.getString(registAuto.getId(),
                (String) registAuto.getDefaultValue());
        return registAccount;
    }


    private void showUpdaterTips(String updateDesc, final boolean force) {
        if (showUpdaterTipsDialog != null) {
            return;
        }
        String negativeText = getString(force ? R.string.settings_logout : R.string.update_next);
        String msg = getString(R.string.new_update_version);
        if (!TextUtils.isEmpty(updateDesc)) {
            msg = updateDesc;
        }
        showUpdaterTipsDialog = ECAlertDialog.buildAlert(this, msg,
                negativeText, getString(R.string.app_update),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showUpdaterTipsDialog = null;
                        if (force) {
                            try {
                                ECPreferences
                                        .savePreference(
                                                ECPreferenceSettings.SETTINGS_FULLY_EXIT,
                                                true, true);
                            } catch (InvalidClassException e) {
                                e.printStackTrace();
                            }
                            restartAPP();
                        }
                    }
                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CCPAppManager.startUpdater(MainAct.this);
                        // restartAPP();
                        showUpdaterTipsDialog = null;
                    }
                });

        showUpdaterTipsDialog.setTitle(R.string.app_tip);
        showUpdaterTipsDialog.setDismissFalse();
        showUpdaterTipsDialog.setCanceledOnTouchOutside(false);
        showUpdaterTipsDialog.setCancelable(false);
        showUpdaterTipsDialog.show();
    }

    public void restartAPP() {

        ECDevice.unInitial();

        Class z = RestServerDefines.QR_APK ? PhoneUI.class : com.yuntongxun.ecdemo.ui.activity.LoginActivity.class;

        Intent intent = new Intent(this, z);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 检测离线消息m
     */
    private void checkOffineMessage() {
        if (SDKCoreHelper.getConnectState() != ECDevice.ECConnectState.CONNECT_SUCCESS) {
            return;
        }
        ECHandlerHelper handlerHelper = new ECHandlerHelper();
        handlerHelper.postDelayedRunnOnThead(new Runnable() {
            @Override
            public void run() {
                boolean result = IMChattingHelper.isSyncOffline();
                if (!result) {
                    ECHandlerHelper.postRunnOnUI(new Runnable() {
                        @Override
                        public void run() {
                            disPostingLoading();
                        }
                    });
                    IMChattingHelper.checkDownFailMsg();
                }
            }
        }, 1000);
    }

    private ECProgressDialog mPostingdialog;

    void showProcessDialog() {
        mPostingdialog = new ECProgressDialog(MainAct.this,
                R.string.login_posting_submit);
        mPostingdialog.show();
    }

    private void disPostingLoading() {
        if (mPostingdialog != null && mPostingdialog.isShowing()) {
            mPostingdialog.dismiss();
        }
    }

    @Override
    public void OnUpdateMsgUnreadCounts() {
        int unreadCount = IMessageSqlManager.qureyAllSessionUnreadCount();
//        int notifyUnreadCount = IMessageSqlManager.getUnNotifyUnreadCount();
        int count = unreadCount;
//        if (unreadCount >= notifyUnreadCount) {
//            count = unreadCount - notifyUnreadCount;
//        }
        if (count > 0) {
            tvUnreadMsgNumber.setVisibility(View.VISIBLE);
            if (count > 99) {
                tvUnreadMsgNumber.setText(getResources().getString(R.string.unread_count_overt_100));
            } else {
                tvUnreadMsgNumber.setText(String.valueOf(unreadCount));
            }
        } else {
            tvUnreadMsgNumber.setVisibility(View.GONE);

        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                v.clearFocus();
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void handlerKickOff(String kickoffText) {
        if (isFinishing()) {
            return;
        }
        ECAlertDialog buildAlert = ECAlertDialog.buildAlert(this, kickoffText,
                getString(R.string.dialog_btn_confim),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ECNotificationManager.getInstance()
                                .forceCancelNotification();
                        restartAPP();
                    }
                });
        buildAlert.setTitle("异地登陆");
        buildAlert.setCanceledOnTouchOutside(false);
        buildAlert.setCancelable(false);
//        buildAlert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        buildAlert.show();
    }

    private void getPersonInfo(final String friendId) {
        Observer<Object> subscriber = new Observer<Object>() {
            @Override
            public void onComplete() {
                LogUtil.e("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(e.toString());
            }

            @Override
            public void onSubscribe(Disposable d) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onNext(Object movieEntity) {
                if (movieEntity != null) {
                    LogUtil.e(movieEntity.toString());
                    ResponseBody body = (ResponseBody) movieEntity;
                    try {
                        String s = new String(body.bytes());
                        ExceptionHandler.logHttpResp(s);
                        String error = "";
                        int code = 0;
                        JSONObject j = new JSONObject(s);
                        if (j != null && j.has("statusMsg")) {
                            error = j.getString("statusMsg");
                        }
                        if (j != null && j.has("statusCode")) {
                            code = Integer.parseInt(j.getString("statusCode"));
                        }
                        if (DemoUtils.isTrue(s)) {
                            Friend friend = DemoUtils.getFriendInfo(s);
                            FriendMessageSqlManager.updateAll(friendId, friend.getNickName(), friend.getFriendState(), friend.getAvatar(), friend.getRemarkName());
                            if (TextUtils.isEmpty(friend.getNickName())) {
                                Intent settingAction = new Intent(MainAct.this, PersonInfoUI.class);
                                settingAction.putExtra("from_regist", true);
                                startActivity(settingAction);
                            }
                        } else {
                            ToastUtil.showMessage(DemoUtils.getErrMsg(s));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        String time = DateUtil.formatNowDate(new Date());
        String url = getSig(time);
        JSONObject map = HttpMethods.buildGetFriendInfo(CCPAppManager.getUserId(), friendId);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), map.toString());
        HttpMethods.getInstance(time).getPersonInfo(subscriber, RestServerDefines.APPKER, url, body);
    }

    private void requestAddFriend(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return;
        }
        Observer<Object> subscriber = new Observer<Object>() {
            @Override
            public void onComplete() {
                System.out.println();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println();
            }

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println();
            }

            @Override
            public void onNext(Object movieEntity) {
                System.out.println();
            }
        };
        String time = DateUtil.formatNowDate(new Date());
        String url = getSig(time);
        JSONObject map = HttpMethods.buildAddFriendOther(CCPAppManager.getUserId(), mobile);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), map.toString());
        HttpMethods.getInstance(time).addFriend(subscriber, RestServerDefines.APPKER, url, body);
    }
}
