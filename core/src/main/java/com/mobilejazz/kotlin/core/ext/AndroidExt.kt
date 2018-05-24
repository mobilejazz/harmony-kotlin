package com.mobilejazz.kotlin.core.ext

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.inputmethod.InputMethodManager

// -------
// Context
// -------

fun Context.getCompatColor(@ColorRes id: Int) = android.support.v4.content.ContextCompat.getColor(this, id)

fun Context.hideKeyboard(view: View) {
  val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.getCompatColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun AppCompatActivity.getCompatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

// --------
// Activity
// --------

inline fun AppCompatActivity.setupActionBar(
  toolbar: Toolbar,
  action: ActionBar.() -> Unit
) {
  setSupportActionBar(toolbar)
  supportActionBar?.run(action)
}

inline fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
  val toolbar: Toolbar = findViewById(toolbarId)
  setupActionBar(toolbar, action)
}

fun AppCompatActivity.addFragment(
  fragment: Fragment,
  frameId: Int,
  tag: String?
) {
  supportFragmentManager.inTransaction { add(frameId, fragment, tag) }
}

fun AppCompatActivity.addFragmentNow(
  fragment: Fragment,
  frameId: Int,
  tag: String?
) {
  supportFragmentManager.inTransactionNow { add(frameId, fragment, tag) }
}

fun AppCompatActivity.addFragmentAllowingStateLoss(
  fragment: Fragment,
  frameId: Int,
  tag: String?
) {
  supportFragmentManager.inTransactionAllowingStateLoss { add(frameId, fragment, tag) }
}

fun AppCompatActivity.addFragmentNowAllowingStateLoss(
  fragment: Fragment,
  frameId: Int,
  tag: String?
) {
  supportFragmentManager.inTransactionNowAllowingStateLoss { add(frameId, fragment, tag) }
}

fun AppCompatActivity.replaceFragment(
  fragment: Fragment,
  frameId: Int,
  tag: String?
) {
  supportFragmentManager.inTransaction { replace(frameId, fragment, tag) }
}

// Keyboards

fun AppCompatActivity.hideKeyboard() = hideKeyboard(if (currentFocus == null) View(this) else currentFocus)

// ---------
// Fragments
// ---------

fun Fragment.addFragment(
  fragment: Fragment,
  frameId: Int,
  tag: String? = null,
  before: (FragmentTransaction.() -> FragmentTransaction)? = null,
  after: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
  activity?.supportFragmentManager?.inTransaction {
    before?.let { before(this) }
    add(frameId, fragment, tag).also { after?.invoke(this) }
  }
}

fun Fragment.addFragmentNow(
  fragment: Fragment,
  frameId: Int,
  tag: String? = null,
  before: (FragmentTransaction.() -> FragmentTransaction)? = null,
  after: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
  activity?.supportFragmentManager?.inTransactionNow {
    before?.let { before(this) }
    add(frameId, fragment, tag).also { after?.invoke(this) }
  }
}

fun Fragment.addFragmentAllowingStateLoss(
  fragment: Fragment,
  frameId: Int,
  tag: String? = null,
  before: (FragmentTransaction.() -> FragmentTransaction)? = null,
  after: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
  activity?.supportFragmentManager?.inTransactionAllowingStateLoss {
    before?.let { before(this) }
    add(frameId, fragment, tag).also { after?.invoke(this) }
  }
}

fun Fragment.addFragmentNowAllowingStateLoss(
  fragment: Fragment,
  frameId: Int,
  tag: String? = null,
  before: (FragmentTransaction.() -> FragmentTransaction)? = null,
  after: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
  activity?.supportFragmentManager?.inTransactionNowAllowingStateLoss {
    before?.let { before(this) }
    add(frameId, fragment, tag).also { after?.invoke(this) }
  }
}

fun Fragment.replaceFragment(
  fragment: Fragment,
  frameId: Int,
  tag: String? = null,
  before: (FragmentTransaction.() -> FragmentTransaction)? = null,
  after: (FragmentTransaction.() -> FragmentTransaction)? = null
) {
  activity?.supportFragmentManager?.inTransaction {
    before?.let { before(this) }
    replace(frameId, fragment, tag).also { after?.invoke(this) }
  }
}

// Toolbars

inline fun Fragment.setupActionBar(
  toolbar: Toolbar,
  action: ActionBar.() -> Unit
) {
  if (activity is AppCompatActivity) {
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    (activity as AppCompatActivity).supportActionBar!!.run(action)
  }
}

inline fun Fragment.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
  val toolbar: Toolbar? = view?.findViewById(toolbarId)
  toolbar?.let { setupActionBar(toolbar, action) }
}

// Keyboards

fun Fragment.hideKeyboard() {
  when {
    view != null -> activity!!.hideKeyboard(view!!)
    else -> {
    }
  }
}

// ----------------
// Fragment Manager
// ----------------

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
  beginTransaction().func().commit()
}

inline fun FragmentManager.inTransactionNow(func: FragmentTransaction.() -> FragmentTransaction) {
  beginTransaction().func().commitNow()
}

inline fun FragmentManager.inTransactionAllowingStateLoss(func: FragmentTransaction.() -> FragmentTransaction) {
  beginTransaction().func().commitAllowingStateLoss()
}

inline fun FragmentManager.inTransactionNowAllowingStateLoss(func: FragmentTransaction.() -> FragmentTransaction) {
  beginTransaction().func().commitNowAllowingStateLoss()
}

// ----------------
// DP & PX
// ----------------

val Int.dp: Int
  get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
  get() = (this * Resources.getSystem().displayMetrics.density).toInt()