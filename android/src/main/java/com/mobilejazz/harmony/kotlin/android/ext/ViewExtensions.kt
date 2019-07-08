package com.mobilejazz.harmony.kotlin.android.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


var View.bg: Int
  get() = 0
  set(@DrawableRes value) = setBackgroundResource(value)

var TextView.textColor: Int
  get() = currentTextColor
  set(@ColorRes v) = setTextColor(context.getCompatColor(v))

var TextView.hintTextColor: Int
  get() = currentHintTextColor
  set(@ColorRes v) = setHintTextColor(context.getCompatColor(v))

fun View.bounce() {
  val animX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.1f, 1f)
  val animY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.1f, 1f)

  AnimatorSet().apply {
    playTogether(animX, animY)
    duration = 350
    interpolator = BounceInterpolator()
  }.start()
}

fun EditText.onTextChanged(block: (CharSequence) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable) {
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      block(s)
    }

  }.also { tag = it })
}

fun EditText.cleanTextListener() {
  if (tag is TextWatcher) {
    removeTextChangedListener(tag as TextWatcher)
  }
}


//this make a bounce when you click the view
fun View.onClick(click: (View) -> Unit) {
  this.setOnClickListener(BounceOnClickListenerDecorator(click))
}

class BounceOnClickListenerDecorator(private val click: (View) -> Unit) : View.OnClickListener {

  override fun onClick(v: View?) {
    v?.let {
      v.bounce()
      click(v)
    }
  }
}

//this delegate is usefull to make custome views because it lets us to invalidate the view whenever a property changes.
fun <Param> View.invalidator(initialValue: Param, afterChangeBlock: ((Param) -> Unit)? = {}): ReadWriteProperty<View, Param> =
    object : ObservableProperty<Param>(initialValue) {
      override fun afterChange(property: KProperty<*>, oldValue: Param, newValue: Param) {
        afterChangeBlock?.invoke(newValue)
        this@invalidator.invalidate()

      }
    }

/**this one is the same as [invalidator] but calling [View.requestLayout] */
fun <Param> View.layoutRequester(initialValue: Param, afterChangeBlock: ((Param) -> Unit)? = {}): ReadWriteProperty<View, Param> =
    object : ObservableProperty<Param>(initialValue) {
      override fun afterChange(property: KProperty<*>, oldValue: Param, newValue: Param) {
        afterChangeBlock?.invoke(newValue)
        this@layoutRequester.requestLayout()

      }
    }


//this delegate is usefull to set properties in a view that you want to be animated (like progress in a progressbar)
fun <Param : Number> View.propertyAnimator(initValue: Param, beforAnimationBlock: ((Param) -> Param)? = null): ReadWriteProperty<Any?, Param> =
    object : ReadWriteProperty<Any?, Param> {

      private var innerValue: Param = initValue
      private val persistedInitialValue = innerValue


      override fun getValue(thisRef: Any?, property: KProperty<*>): Param {
        return innerValue
      }

      @Suppress("UNCHECKED_CAST")
      override fun setValue(thisRef: Any?, property: KProperty<*>, value: Param) {

        val finalValue = beforAnimationBlock?.invoke(value) ?: value

        obtainValueAnimator(persistedInitialValue, finalValue).apply {
          duration = 1500L
          interpolator = BounceInterpolator()
          addUpdateListener { innerValue = it.animatedValue as Param; invalidate() }
        }.start()
      }

      private fun obtainValueAnimator(innerValue: Param, value: Param): ValueAnimator {
        return when (innerValue) {
          is Float -> ValueAnimator.ofFloat(innerValue, value.toFloat())
          is Double -> ValueAnimator.ofFloat(innerValue.toFloat(), value.toFloat())
          is Int -> ValueAnimator.ofInt(innerValue, value.toInt())
          is Short -> ValueAnimator.ofInt(innerValue.toInt(), value.toInt())
          is Long -> ValueAnimator.ofInt(innerValue.toInt(), value.toInt())
          else -> ValueAnimator.ofFloat(innerValue.toFloat(), value.toFloat())
        }
      }

    }


//this delegate allows to define a integer value (defining the alpha) and it animates it with a bounce animation.
fun alphaWithBounceAnimator(): ReadWriteProperty<ImageView, Int> =
    object : ReadWriteProperty<ImageView, Int> {

      override fun getValue(thisRef: ImageView, property: KProperty<*>): Int {
        return thisRef.imageAlpha
      }

      override fun setValue(thisRef: ImageView, property: KProperty<*>, value: Int) {

        val animX = ObjectAnimator.ofFloat(thisRef, "scaleX", 1f, 1.25f, 1f)
        val animY = ObjectAnimator.ofFloat(thisRef, "scaleY", 1f, 1.25f, 1f)
        val alpha = ObjectAnimator.ofInt(thisRef.imageAlpha, value).apply {
          addUpdateListener { thisRef.imageAlpha = it.animatedValue as Int }
        }

        if (thisRef.imageAlpha != value) {
          AnimatorSet().apply {
            playTogether(animX, animY, alpha)
            duration = 150L
            interpolator = AccelerateDecelerateInterpolator()

          }.start()
        }
      }

    }

//using the alphaWithBounceAnimator we define a imageAlfa extender property for each imageview.
//that makes the animation.
var ImageView.imageAlfa: Int by alphaWithBounceAnimator()


//the same as alphaWithBounceAnimator but for views in general
fun alphaWithBounceAnimatorForView(): ReadWriteProperty<View, Float> =
    object : ReadWriteProperty<View, Float> {

      override fun getValue(thisRef: View, property: KProperty<*>): Float {
        return thisRef.alpha
      }

      override fun setValue(thisRef: View, property: KProperty<*>, value: Float) {

        val animX = ObjectAnimator.ofFloat(thisRef, "scaleX", 1f, 1.25f, 1f)
        val animY = ObjectAnimator.ofFloat(thisRef, "scaleY", 1f, 1.25f, 1f)
        val alpha = ObjectAnimator.ofFloat(thisRef.alpha, value).apply {
          addUpdateListener { thisRef.alpha = it.animatedValue as Float }
        }

        if (thisRef.alpha != value) {
          AnimatorSet().apply {
            playTogether(animX, animY, alpha)
            duration = 350L
            interpolator = AccelerateDecelerateInterpolator()
          }.start()
        }
      }

    }

var View.alfa: Float by alphaWithBounceAnimatorForView()