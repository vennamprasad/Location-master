package com.appyhigh.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.appyhigh.R
import com.appyhigh.databinding.ActivityPermissionsBinding
import com.appyhigh.utils.PermissionUtils.Companion.contactsPermissionsSet
import com.appyhigh.utils.PermissionUtils.Companion.filePermissions
import com.appyhigh.utils.PermissionUtils.Companion.hasPermissions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionsActivity : AppCompatActivity(), View.OnClickListener
{
	val TAG = "PermissionActivity"
	val DEFAULT_MESSAGING_APP = 1
	val PERMISSION_ALL = 2
	val NOTIFICATION = 3
	lateinit var binding: ActivityPermissionsBinding
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_permissions)
		val window = window
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
		window.statusBarColor = ContextCompat.getColor(this, R.color.white)
		binding.smsLayout.setOnClickListener(this)
		binding.contactLayout.setOnClickListener(this)
		binding.notificationLayout.setOnClickListener(this)
		binding.skip.setOnClickListener(this)
		setSMSLayout()
		setNotificationLayout()
		Log.e(TAG, "Notification : " + NotificationManagerCompat.from(this).areNotificationsEnabled())
		moveToNextActivity()
	}
	
	private fun setSMSLayout()
	{
		if (Telephony.Sms.getDefaultSmsPackage(this) == packageName)
		{
			binding.smsLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.permission_active))
			binding.smsImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.permission_active))
			binding.smsTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
			binding.smsSubtitle.setTextColor(ContextCompat.getColor(this, R.color.white))
		} else
		{
			binding.smsLayout.background = ContextCompat.getDrawable(this, R.drawable.round_permission_inactive)
			binding.smsImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.permission_inactive))
			binding.smsTitle.setTextColor(ContextCompat.getColor(this, R.color.permission_title))
			binding.smsSubtitle.setTextColor(ContextCompat.getColor(this, R.color.permission_title))
		}
	}
	
	private fun setNotificationLayout()
	{
		if (NotificationManagerCompat.from(this).areNotificationsEnabled())
		{
			binding.notificationLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.permission_active))
			binding.notificationImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.permission_active))
			binding.notificationTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
			binding.notificationSubtitle.setTextColor(ContextCompat.getColor(this, R.color.white))
		} else
		{
			binding.notificationLayout.background = ContextCompat.getDrawable(this, R.drawable.round_permission_inactive)
			binding.notificationImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.permission_inactive))
			binding.notificationTitle.setTextColor(ContextCompat.getColor(this, R.color.permission_title))
			binding.notificationSubtitle.setTextColor(ContextCompat.getColor(this, R.color.permission_title))
		}
	}
	
	private fun setPermissionActive()
	{
		binding.contactLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.permission_active))
		binding.contactImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.permission_active))
		binding.contactTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
		binding.contactSubtitle.setTextColor(ContextCompat.getColor(this, R.color.white))
	}
	
	private fun setPermissionInactive()
	{
		binding.contactLayout.background = ContextCompat.getDrawable(this, R.drawable.round_permission_inactive)
		binding.contactImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.permission_inactive))
		binding.contactTitle.setTextColor(ContextCompat.getColor(this, R.color.permission_title))
		binding.contactSubtitle.setTextColor(ContextCompat.getColor(this, R.color.permission_title))
	}
	
	override fun onClick(v: View?)
	{
		when (v?.id)
		{
			R.id.sms_layout          -> checkDefaultSettings()
			R.id.contact_layout      -> checkPermission()
			R.id.notification_layout -> notification()
			R.id.skip                -> startActivity(Intent(this, DashboardActivity::class.java))
		}
	}
	
	private fun checkDefaultSettings(): Boolean
	{
		val isDefault: Boolean
		isDefault = if (Telephony.Sms.getDefaultSmsPackage(this) != packageName)
		{
			val builder = MaterialAlertDialogBuilder(this)
			builder.setMessage("Change Default SMS App").setCancelable(false).setPositiveButton("Yes") { dialog, id ->
				val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
				intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
				startActivityForResult(intent, DEFAULT_MESSAGING_APP)
			}.setNegativeButton("No") { dialog, id ->
				dialog.dismiss()
			}
			val alert = builder.create()
			alert.show()
			false
		} else true
		return isDefault
	}
	
	private fun notification()
	{
		val intent = Intent()
		intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
		intent.putExtra("app_package", packageName)
		intent.putExtra("app_uid", applicationInfo.uid)
		intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
		startActivityForResult(intent, NOTIFICATION)
	}
	
	private fun checkPermission()
	{
		if (!hasPermissions(this, *contactsPermissionsSet))
		{
			setPermissionInactive()
			ActivityCompat.requestPermissions(this, contactsPermissionsSet, PERMISSION_ALL)
		} else
		{
			setPermissionActive()
		}
	}
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode)
		{
			PERMISSION_ALL ->
			{
				checkPermission()
				moveToNextActivity()
			}
		}
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode)
		{
			DEFAULT_MESSAGING_APP ->
			{
				run {
					setSMSLayout()
					checkPermission()
					moveToNextActivity()
				}
			}
			NOTIFICATION          ->
			{
				setNotificationLayout()
				moveToNextActivity()
			}
		}
	}
	
	private fun moveToNextActivity()
	{
		var allPermissionGiven = 0
		if (Telephony.Sms.getDefaultSmsPackage(this) == packageName)
		{
			allPermissionGiven++
		}
		if (hasPermissions(this, *filePermissions))
		{
			allPermissionGiven++
		}
		if (NotificationManagerCompat.from(this).areNotificationsEnabled())
		{
			allPermissionGiven++
		}
		if (allPermissionGiven == 3)
		{
			startActivity(Intent(this, DashboardActivity::class.java))
		}
	}
	
}