package com.taskail.mixion.activity

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.taskail.mixion.R

/**
 *Created by ed on 1/19/18.
 */

abstract class SingleFragmentActivity<T : Fragment>: AppCompatActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setContentView(getLayout())

        if (!isFragmentCreated()) {
            addFragment(createFragment())
        }

        Log.d(TAG, "onCreate")
    }

    protected fun addFragment(fragment: T) {
        supportFragmentManager.beginTransaction().add(getContainerId(), fragment).commit()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun getFragment(): T? {
        return supportFragmentManager.findFragmentById(getContainerId()) as T?
    }

    protected abstract fun createFragment(): T

    @LayoutRes
    protected open fun getLayout() : Int{
        return R.layout.activity_single_fragment
    }
    @IdRes
    protected fun getContainerId() : Int{
        return R.id.fragment_container
    }

    protected fun isFragmentCreated(): Boolean {
        return getFragment() != null
    }
}