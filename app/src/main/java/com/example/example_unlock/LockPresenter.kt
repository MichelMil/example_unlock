package com.example.example_unlock

import android.os.Handler
import android.view.View
import android.widget.ImageView
import com.example.example_unlock.databinding.ActivityMainBinding
import java.util.concurrent.locks.Lock

class LockPresenter(private val target:ILock) {
    private lateinit var binding:ActivityMainBinding

    //保存九个点
    private lateinit var dotArray: ArrayList<ImageView>


    //记录上一次被点亮的视图
    private var lastSelectedDot: ImageView? = null
    //记录密码
    private val passwordBuilder = StringBuilder()
    //记录所有点亮的控件
    private val selectedArray = arrayListOf<ImageView>()

    //初始化数据
    fun initData(binding : ActivityMainBinding){
        this.binding = binding
        //将九个点的视图保存到数组中
        val dotArray = arrayListOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4,
            binding.dot5,
            binding.dot6,
            binding.dot7,
            binding.dot8,
            binding.dot9
        )
        //竖线
        val verticalLineArray = arrayListOf(
            binding.line14,
            binding.line25,
            binding.line36,
            binding.line47,
            binding.line58,
            binding.line69
        )
        //横线
        val landscapeLineArray = arrayListOf(
            binding.line12,
            binding.line23,
            binding.line45,
            binding.line56,
            binding.line78,
            binding.line89
        )
        //左斜
        val leftSlashLineArray = arrayListOf(binding.line24, binding.line35, binding.line57, binding.line68)
        //右斜
        val rightSlashLineArray = arrayListOf(binding.line15, binding.line26, binding.line48, binding.line59)

        LockModel().addModel(dotArray,R.drawable.dot_normal, R.drawable.dot_selected)
        LockModel().addModel(verticalLineArray, R.drawable.line_1_normal, R.drawable.line_1_error)
        LockModel().addModel(landscapeLineArray, R.drawable.line_2_normal, R.drawable.line_2_error)
        LockModel().addModel(leftSlashLineArray, R.drawable.line_3_normal, R.drawable.line_3_error)
        LockModel().addModel(rightSlashLineArray, R.drawable.line_4_normal, R.drawable.line_4_error)

    }

    fun touchdown(x:Float,y:Float){
        //判断触摸点是否在原点内部
        val dotView = isInView(x, y)
        if (dotView != null) {
            //点亮原点
            target.changeVisibility(dotView)
            //记录下来
            lastSelectedDot = dotView
            //记录密码
            passwordBuilder.append(dotView.tag as String)
            //保存
            selectedArray.add(dotView)
        }

    }

    fun touchmove(x:Float,y:Float){
        //判断触摸点是否在原点内部
        val dotView = isInView(x,y)
        //处理在点亮的点内部触发move事件
        if (lastSelectedDot != dotView) {
            if (dotView != null) {
                //判断是否是第一个点
                if (lastSelectedDot == null) {
                    target.changeVisibility(dotView)
                    lastSelectedDot = dotView
                    //记录密码
                    passwordBuilder.append(dotView.tag as String)
                    selectedArray.add(dotView)
                } else {
                    //判断路线是否有
                    //获取上一个点和当前点的tag值 形成线的tag
                    val lastTag = (lastSelectedDot!!.tag as String).toInt()
                    val currentTag = (dotView.tag as String).toInt()
                    //形成线的tag small*10 +big
                    val lineTag =
                        if (lastTag < currentTag) lastTag * 10 + currentTag else currentTag * 10 + lastTag
                    //获取lineTag对应的控件
                    val lineView =
                        binding.container.findViewWithTag<ImageView>("$lineTag")
                    if (lineView != null) {
                        //有路线
                        target.changeVisibility(dotView)
                        target.changeVisibility(lineView)

                        lastSelectedDot = dotView
                        //记录密码
                        passwordBuilder.append(dotView.tag as String)
                        //保存
                        selectedArray.add(dotView)
                        selectedArray.add(lineView)
                    }
                }
            }
        }
    }

    fun touchup(x: Float,y: Float){
        binding.alertTitle.text = passwordBuilder.toString()
        //判断密码是否正确
        if (passwordBuilder.toString() == LockModel().getPassword()) {
            //密码正确
            binding.alertTitle.text = "密码解锁成功"
            passwordBuilder.clear()
        } else {
            binding.alertTitle.text = "密码解锁失败"
            //切换图片
            selectedArray.forEach {
                //找到这个控件对应的model
                for(model:Model in LockModel().getModels()){
                    if (model.view == it) {
                        target.changeImage(model,false)
                        passwordBuilder.clear()
                        break
                    }
                }
            }
        }
        Handler().postDelayed(
            {
                selectedArray.forEach {
                    target.changeVisibility(it)
                    //找到这个控件对应的model
                    for(model:Model in LockModel().getModels()) {
                        if (model.view == it) {
                            target.changeImage(model,true)
                            break
                        }
                    }
                }
            }, 500
        )
    }

    /**
     * 判断触摸点是否在某个原点内部
     * 在：返回这个原点对象
     * 不在：返回空 null
     */
    fun isInView(x: Float, y: Float): ImageView? {
        dotArray.forEach {
            if ((x >= it.left && x <= it.right) && (y >= it.top && y <= it.bottom)) {
                return it
            }
        }
        return null
    }

}