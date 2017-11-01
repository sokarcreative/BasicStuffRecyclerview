package com.sokarcreative.basicstuffrecyclerview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import com.sokarcreative.basicstuffrecyclerview.models.Message
import com.sokarcreative.basicstuffrecyclerview.models.TitleH
import com.sokarcreative.library.BasicStuffItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mDemoAdapter: DemoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.mDemoAdapter = DemoAdapter(randomValues(), this)

        recyclerView?.adapter = mDemoAdapter
        val basicStuffItemDecoration = BasicStuffItemDecoration(mDemoAdapter!!)
        recyclerView?.addItemDecoration(basicStuffItemDecoration)
        recyclerView?.addOnItemTouchListener(basicStuffItemDecoration)
    }



    fun onNotifyDataSetChangedClick(view: View) {
        mDemoAdapter?.notifyDataSetChanged()
        if(view.id == R.id.checkBoxShowFirstLastDecoration && view is CheckBox && view.isChecked && mDemoAdapter!!.itemCount > 0){
            recyclerView.smoothScrollToPosition(0)
        }
    }

    fun onRandomValuesClick(view: View) {
        mDemoAdapter?.refresh(randomValues())
        if(view.id == R.id.checkboxContentBeforeHeader && view is CheckBox && view.isChecked){
            recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun randomValues(): List<Any> {
        val items = ArrayList<Any>()
        val c = Calendar.getInstance()
        val currentYear = c.get(Calendar.YEAR)
        for (year in 0..3) {
            if (year == 0 && checkboxContentBeforeHeader.isChecked) {
                items.add(Message("Walt", "Are you sure? That's right. Now. Say my name."))
                items.add(Message("Declan", "You're Heisenberg."))
                items.add(Message("Walt", "You're goddamn right."))
            }
            c.set(Calendar.YEAR, currentYear + year)
            if (shouldDisplayH(checkboxH1.isChecked, checkboxRandomHeader.isChecked)) items.add(TitleH(TitleH.H1, c.timeInMillis))
            for (month in (if (year != 0) 0 else c.get(Calendar.MONTH))..11) {
                if (Util.randInt(0, 3) == 1) {
                    c.set(Calendar.MONTH, month)
                    if (shouldDisplayH(checkboxH2.isChecked, checkboxRandomHeader.isChecked)) items.add(TitleH(TitleH.H2, c.timeInMillis))
                    val daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH)
                    for (day in (if (year == 0 && month == c.get(Calendar.MONTH)) c.get(Calendar.DAY_OF_MONTH) else 0) until daysInMonth) {
                        if (Util.randInt(0, 10) == 1) {
                            c.set(Calendar.DAY_OF_MONTH, day)
                            if (shouldDisplayH(checkboxH3.isChecked, checkboxRandomHeader.isChecked)) items.add(TitleH(TitleH.H3, c.timeInMillis))
                            val randomContentLength = Util.randInt(1, 10)
                            for (x in 0 until randomContentLength) {
                                items.add(Message("Walt", "Here is a message"))
                            }
                        }
                    }
                }
            }
        }
        return items
    }

    private fun shouldDisplayH(displayH: Boolean, needRandom: Boolean): Boolean {
        return displayH && !needRandom || displayH && Util.randInt(0, 1) == 1
    }
}
