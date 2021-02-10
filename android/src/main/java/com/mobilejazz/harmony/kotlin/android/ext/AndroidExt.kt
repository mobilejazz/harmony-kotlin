package com.mobilejazz.harmony.kotlin.android.ext

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

// -------
// Context
// -------

fun Context.getCompatColor(@ColorRes id: Int): Int {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) getColor(id)
  else resources.getColor(id)
}

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

fun AppCompatActivity.hideKeyboard() = hideKeyboard(if (currentFocus == null) View(this) else currentFocus!!)

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

/**
 * Transforms DPs to pixels
 * @receiver Int: the DPs
 * @return the number of pixels for DP
 */
fun Int.dp(ctx: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), ctx.resources.displayMetrics)

fun Float.dp(ctx: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, ctx.resources.displayMetrics)

/**
 * Transforms SPs to pixels
 * @receiver Int: the SPs
 * @return the number of pixels for SP
 */
fun Int.sp(ctx: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), ctx.resources.displayMetrics)

fun Float.digits(numOfDigits: Int): String {
  return String.format("%.${numOfDigits}f", this)
}

// ----------------
// Dialogs
// ----------------
fun Fragment.showInfo(message: String) {
  AlertDialog.Builder(this.activity!!)
      .setMessage(message)
      .create()
      .show()
}
