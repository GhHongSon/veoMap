package com.pks.veo.ui.theme

import android.content.Context
import android.location.Location

/**
 * describe:
 * author：pang kang shun
 * date: 2025/7/4 23:00
 */
interface LocationService {
    // 获取最后一次已知位置
    fun getLastKnownLocation(callback: (Location?) -> Unit)

    // 开始位置更新
    fun startLocationUpdates(callback: (Location) -> Unit)

    // 停止位置更新
    fun stopLocationUpdates()

    // 检查位置权限
    fun hasLocationPermission(context: Context): Boolean

    // 检查GPS是否开启
    fun isGpsEnabled(context: Context): Boolean
}