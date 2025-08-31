package com.example.tincoderapplication.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadcastReceiverMusic: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action=intent?.getIntExtra("action",0)?:0
        if(action!=0){
           val intentService=Intent(context, MyService::class.java)
            intentService.putExtra("action",action)
            context?.startService(intentService)
        }
    }
}