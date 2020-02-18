package com.appyhigh.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.appyhigh.R
import com.appyhigh.utils.PermissionUtils

class SplashActivity : AppCompatActivity()
{
	
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)
		val handler = Handler()
		handler.postDelayed({ moveToNextActivity() }, 1000)
	}
	
	private fun moveToNextActivity()
	{
		var allPermissionGiven = 0
		if (Telephony.Sms.getDefaultSmsPackage(this) == packageName)
		{
			allPermissionGiven++
		}
		if (PermissionUtils.hasPermissions(this, *PermissionUtils.contactsPermissionsSet))
		{
			allPermissionGiven++
		}
		if (NotificationManagerCompat.from(this).areNotificationsEnabled())
		{
			allPermissionGiven++
		}
		if (allPermissionGiven == 3)
		{
			startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
			finish()
		} else
		{
			startActivity(Intent(this@SplashActivity, PermissionsActivity::class.java))
			finish()
		}
	}
	
}
