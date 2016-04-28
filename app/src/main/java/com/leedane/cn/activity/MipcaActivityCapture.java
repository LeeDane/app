package com.leedane.cn.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.FanHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.Base64Util;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.zxing.camera.CameraManager;
import com.leedane.cn.zxing.decoding.CaptureActivityHandler;
import com.leedane.cn.zxing.decoding.InactivityTimer;
import com.leedane.cn.zxing.view.ViewfinderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
/**
 * 我的扫码activity
 * Created by LeeDane on 2016/4/14.
 */
public class MipcaActivityCapture extends Activity implements Callback, TaskListener {

	private static final int GET_SYSTEM_IMAGE_CODE = 1034;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private boolean isLogin;

	private Button mButtonScanImage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		mButtonScanImage = (Button)findViewById(R.id.scan_image);
		mButtonScanImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//调用系统图库
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				intent.putExtra("crop", true);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, GET_SYSTEM_IMAGE_CODE);
			}
		});
		/*Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MipcaActivityCapture.this.finish();
				
			}
		});*/

		isLogin = BaseApplication.isLogin();
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * ����ɨ����
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		}else {
			dealScanResult(resultString);
		}
	}

	/**
	 * 处理扫描二维码后的结果
	 * @param resultString
	 */
	private void dealScanResult(String resultString){
		//内部链接
		if(resultString.startsWith("leedane:")){
			resultString = resultString.substring("leedane:".length(), resultString.length());
			if(StringUtil.isNotNull(resultString)){
				try{
					JSONObject jsonObject = new JSONObject(new String(Base64Util.decode(resultString.toCharArray())));
					if(jsonObject.has("tableName") && jsonObject.has("tableId")){//打开详情
						//启动详情的activity
						CommonHandler.startDetailActivity(MipcaActivityCapture.this,jsonObject.getString("tableName"), jsonObject.getInt("tableId"), null );
						//关闭当前的扫码页面
						finish();
					}else if(jsonObject.has("account") && jsonObject.has("id")){ //打开用户个人中心
						showUserInfoDialog(jsonObject.getString("account"), jsonObject.getInt("id"));
					}else{
						ToastUtil.failure(MipcaActivityCapture.this, "暂时不支持的类型，请更新最新版本后尝试");
						return;
					}
				}catch (JSONException e){
					e.printStackTrace();
					ToastUtil.failure(MipcaActivityCapture.this, "数据解析失败！");
					return;
				}

			}
			//网络链接
		}else if(resultString.startsWith("http://") || resultString.startsWith("https://")){
			CommonHandler.openLink(MipcaActivityCapture.this, resultString);
			//无法打开的链接(提供文字复制功能，尝试网络打开链接的功能)
		}else{

		}
	}

	private Dialog mUserInfoDialog;
	private Button mUserInfoCancel;
	private Button mUserInfoPersonal;
	private Button mUserInfoAttention;
	/**
	 * 展示用户中心的弹出框
	 * @param account
	 * @param uid
	 */
	private void showUserInfoDialog(String account, final int uid){
		dismissUserInfoDialog();
		mUserInfoDialog = new Dialog(MipcaActivityCapture.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
		View view = LayoutInflater.from(MipcaActivityCapture.this).inflate(R.layout.mipca_user_info, null);

		CircularImageView imageView = (CircularImageView)view.findViewById(R.id.mipca_user_info_img);
		TextView tip = (TextView)view.findViewById(R.id.mipca_user_info_tip);
		TextView username = (TextView)view.findViewById(R.id.mipca_user_name);
		username.setText(StringUtil.changeNotNull(account));

		mUserInfoCancel = (Button)view.findViewById(R.id.mipca_user_info_cancel);
		mUserInfoCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissUserInfoDialog();
				finish();
			}
		});
		mUserInfoPersonal = (Button)view.findViewById(R.id.mipca_user_info_personal);
		mUserInfoPersonal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonHandler.startPersonalActivity(MipcaActivityCapture.this, uid);
			}
		});
		if(isLogin){
			mUserInfoAttention = (Button)view.findViewById(R.id.mipca_user_info_add);
			mUserInfoAttention.setVisibility(View.VISIBLE);
			mUserInfoAttention.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FanHandler.addAttention(MipcaActivityCapture.this, uid);
				}
			});
		}
		mUserInfoDialog.setTitle("检测结果");
		mUserInfoDialog.setCancelable(true);
		mUserInfoDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dismissUserInfoDialog();
			}
		});
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(BaseApplication.newInstance().getScreenWidthAndHeight()[0]-100, 700);
		mUserInfoDialog.setContentView(view, params);
		mUserInfoDialog.show();
	}

	private  void dismissUserInfoDialog(){
		if(mUserInfoDialog != null && mUserInfoDialog.isShowing()){
			mUserInfoDialog.dismiss();
		}
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void taskStarted(TaskType type) {

	}

	@Override
	public void taskFinished(TaskType type, Object result) {
		if(result instanceof Error){
			Toast.makeText(getBaseContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT).show();
			dismissLoadingDialog();
			return;
		}

		try{
			JSONObject jsonObject = new JSONObject(String.valueOf(result));
			//执行添加关注操作
			if(type == TaskType.ADD_FAN){
				dismissLoadingDialog();
				if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
					ToastUtil.success(MipcaActivityCapture.this, jsonObject);
					if(mUserInfoAttention != null){
						//设置不可点击
						mUserInfoAttention.setClickable(false);
						mUserInfoAttention.setText(getResources().getString(R.string.personal_is_fan));
					}
				}else{
					ToastUtil.failure(MipcaActivityCapture.this, jsonObject);
				}
				return;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void taskCanceled(TaskType type) {

	}
	/**
	 * 弹出加载ProgressDiaLog
	 */
	private ProgressDialog mProgressDialog;
	/**
	 * 显示加载Dialog
	 * @param title  标题
	 * @param main  内容
	 * @param cancelable 是否可以取消
	 */
	protected void showLoadingDialog(String title, String main, boolean cancelable){
		dismissLoadingDialog();
		mProgressDialog = ProgressDialog.show(MipcaActivityCapture.this, title, main, true, cancelable);
	}
	/**
	 * 隐藏加载Dialog
	 */
	protected void dismissLoadingDialog(){
		if(mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == GET_SYSTEM_IMAGE_CODE) {
				final Uri uri = data.getData();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						Result result = scanningImage(uri.getPath());

						if (result != null) {
							String str = result.getText();
							if (!str.equals("")) {
								dealScanResult(str);
								return;
							}
						}
						Toast.makeText(MipcaActivityCapture.this, "扫描图片失败", Toast.LENGTH_SHORT).show();

						//Toast.makeText(MipcaActivityCapture.this, "Scan failed--->"+decode(uri.getPath()), Toast.LENGTH_SHORT).show();
					}
				}, 500);

			}
		}
	}
	private Bitmap scanBitmap;

	/**
	 * 解析二维码图片
	 * @param path
	 * @return
	 */
	protected Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		// DecodeHintType 和EncodeHintType
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);

		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*public String decode(String imgPath) {
		Result result = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true; // 先获取原大小
			scanBitmap = BitmapFactory.decodeFile(imgPath, options);
			options.inJustDecodeBounds = false; // 获取新的大小

			int sampleSize = (int) (options.outHeight / (float) 200);

			if (sampleSize <= 0)
				sampleSize = 1;
			options.inSampleSize = sampleSize;
			scanBitmap = BitmapFactory.decodeFile(imgPath, options);
			RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

			result = new MultiFormatReader().decode(bitmap, hints);
			return result.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}*/
}