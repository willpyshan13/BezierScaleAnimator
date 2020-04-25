package cn.will.anim.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import android.view.ViewGroup
import android.widget.ImageView


/**
 *
 * Desc:贝塞尔曲线view
 * <p>
 * Date: 2020-04-22
 * Copyright: Copyright (c) 2018 - 2020
 * Company: @微微科技有限公司
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
object BezierUtils {

    /**
     *
     * Desc:贝塞尔曲线加入购物车
     * <p>
     * Author: pengyushan
     * Date: 2020-04-22
     * @param context Context             上下文
     * @param sourceView ImageView        源文件
     * @param viewGroup ViewGroup         外面的布局
     * @param endLocation IntArray        终点坐标
     */
    @JvmStatic
    fun addToCartAnimal(context: Context, sourceView: ImageView, viewGroup: ViewGroup, endLocation: IntArray) {
        //创建一个图片用来放这个视图，并添加到父视图中
        val bezierView = ImageView(context)
        Glide.with(context).load(sourceView.drawable).apply(RequestOptions().circleCrop()).into(bezierView)
        val params = ConstraintLayout.LayoutParams(sourceView.width, sourceView.height)
        viewGroup.addView(bezierView, params)
        //存放当前位置的坐标
        val currentLocation = FloatArray(2)
        //获取图片起始坐标
        val startLocation = IntArray(2)
        sourceView.getLocationInWindow(startLocation)
        //起始坐标点
        val startX = startLocation[0].toFloat()
        val startY = startLocation[1].toFloat()
        //终点坐标
        val endX = (endLocation[0] - sourceView.width / 2 ).toFloat()
        val endY = (endLocation[1] - sourceView.height / 2 ).toFloat()

        //绘制贝塞尔曲线
        val path = Path().apply {
            this.moveTo(startX, startY)
            this.quadTo((startX + endX) / 2, startY, endX, endY)
        }
        //计算贝塞尔曲线的坐标
        val pathMeasure = PathMeasure(path, false)
        bezierView.x = startX
        bezierView.y = startY
        //动画集合
        val animatorSet = AnimatorSet()
        //缩放动画
        val scaleAnim = ValueAnimator.ofFloat(1F, 0.1F)
        scaleAnim.duration = 100
        scaleAnim.addUpdateListener {
            val value = it.animatedValue as Float
            bezierView.scaleX = value
            bezierView.scaleY = value
        }
        //平移动画
        val translateAnim = ValueAnimator.ofFloat(0F, pathMeasure.length)
        translateAnim.addUpdateListener {
            val value = it.animatedValue as Float
            pathMeasure.getPosTan(value, currentLocation, null)
            bezierView.x = currentLocation[0]
            bezierView.y = currentLocation[1]
        }
        translateAnim.startDelay = 300
        translateAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                //do something
            }

            override fun onAnimationEnd(animation: Animator?) {
                viewGroup.removeView(bezierView)
                RxBus.getDefault().post(CartRefreshEvent())
            }

            override fun onAnimationCancel(animation: Animator?) {
                //do something
            }

            override fun onAnimationStart(animation: Animator?) {
                //do something
            }

        })
        animatorSet.playSequentially(scaleAnim, translateAnim)
        animatorSet.start()
    }
}