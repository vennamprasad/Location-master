package com.appyhigh.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionUtils
{
	companion object
	{
		val contactsPermissionsSet = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_SMS)
		val filePermissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_SMS)
		
		fun hasPermissions(context: Context?, vararg permissions: String?): Boolean
		{
			if (context != null)
			{
				for (permission in permissions)
				{
					if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED)
					{
						return false
					}
				}
			}
			return true
		}
	}
}