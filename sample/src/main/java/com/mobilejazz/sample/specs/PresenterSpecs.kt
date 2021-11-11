package com.mobilejazz.sample.specs

import com.harmony.kotlin.common.WeakReference
import com.harmony.kotlin.common.logger.Logger

// Presenter is always defined as an interface
interface CoffeeMachinePresenter {

  /* region Standard Methods */

  // onEvent prefix will be used to refer to an event that was not triggered by the user (example: notification received)
  fun onEventWaterLevelLow()

  // onAction prefix will be used to refer to an user-triggered event (example: button pressed, device rotated)
  // Avoid platform-specific conventions like onActionShouldPrepareCoffee or prepareCoffee(withSize: Int)
  // Avoid UI-dependant references like onActionLoginButtonPressed or onActionPopupDismissed
  fun onActionPrepareCoffee(size: Int)

  /* endregion */

  /* region UI life cycle */

  /*
   * Some methods might be needed to setup internal properties or trigger data refresh.
   * If that's the case try to  use the following naming
   */
  // View finished loading and can be setup
  fun onViewLoaded()

  // View was already loaded but will appear again on screen
  // Notice. Sometimes a method might need different params to initialize in iOS and / or Android
  // If that's the case you can use params with default value so only the platforms which need them will use them
  fun onViewRefresh(sleepModeEnabled: Boolean = false)

  // View will be destroyed
  fun onViewDealloc()

  /* endregion */

  /* region View */

  /*
   * An interface View must be provided so the presenter can react to both platforms
   * The views basically will provide three kind of methods:
   * - onDisplay to show data
   * - onNotify to let UI now that some action must me taken
   * - onFailed to notify errors
   * Try to be verbose on the signature. onDisplayCoffeeProgress(percent: Int) better than onDisplay(coffeProgress: Int)
   */
  interface View {

    fun onDisplayWaterLevelLow()

    fun onDisplayCoffeeProgress(percent: Int)

    fun onFailedToDisplayCoffeeProgress(e: Exception)

    fun onNotifyCoffeeReady()

    // Provide default implementations so the UI is not forced to implement a method that is not strictly necessary
    fun onDisplayWeatherOutside(degrees: Float) {}
  }

  /* endregion */
}

/* region implementation */
class CoffeeMachineDefaultPresenter(
  // IMPORTANT: First parameter always the view as a WeakReference
  private val view: WeakReference<CoffeeMachinePresenter.View>,
  private val getWatterLevelInteractor: GetWatterLevelInteractor,
  private val logger: Logger
) : CoffeeMachinePresenter {

  // Avoid local vars. Use the interactor instead
  var waterLevel: Int = 100

  override fun onActionPrepareCoffee(size: Int) {

    // Remember to always handle exceptions. In iOS is very difficult to debug!
    try {

      // Wrong
      if (waterLevel < 20) {
        view.get()?.onDisplayWaterLevelLow()
      }

      // Good
      if (getWatterLevelInteractor() < 20) {
        view.get()?.onDisplayWaterLevelLow()
      }

      // Prepare coffee

      view.get()?.onDisplayCoffeeProgress(100)
    } catch (e: Exception) {
      view.get()?.onFailedToDisplayCoffeeProgress(e)
    }
  }

  override fun onEventWaterLevelLow() {
    try {
      // ... Whatever dangerous logic you do always inside try/catch
      view.get()?.onDisplayWaterLevelLow()
    } catch (e: Exception) {
      // If there is no fail at least notify Bugfender
      logger.e("Error making coffee: " + e.message)
    }
  }

  override fun onViewLoaded() {
  }

  override fun onViewRefresh(sleepModeEnabled: Boolean) {
  }

  override fun onViewDealloc() {
  }
}

/* endregion */

class GetWatterLevelInteractor() {
  operator fun invoke(): Int {
    return 23
  }
}
