package by.nalivajr.margosample.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import by.nalivajr.margo.annonatations.BindView
import by.nalivajr.margo.annonatations.InnerView
import by.nalivajr.margo.annonatations.OnCheckChanged
import by.nalivajr.margo.annonatations.OnClick
import by.nalivajr.margo.tools.Margo
import by.nalivajr.margosample.R

class TestActivity : AppCompatActivity() {

    @BindView(R.id.tv_first)
    private lateinit var first: TextView

    @BindView(R.id.tv_second)
    private lateinit var second: TextView

    @BindView(123, required = true)
    private lateinit var fakeView: TextView

    @InnerView(R.id.cb_box)
    private lateinit var box: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        Margo.bind(this)
    }

    @OnClick(R.id.tv_second)
    private fun onSecondClicked() {
        Toast.makeText(this, "No arg second [${second.text}] called", Toast.LENGTH_SHORT).show()
    }

    @OnClick(5)
    private fun onFakeClicked() {
        Toast.makeText(this, "Fake view click", Toast.LENGTH_SHORT).show()
    }

    @OnClick(R.id.tv_first)
    private fun onSecondClicked(v: View) {
        Toast.makeText(this, "One view arg first called", Toast.LENGTH_SHORT).show()
    }

    @OnClick(value = [(R.id.tv_first), (R.id.tv_second)])
    private fun onSecondClicked(v: TextView) {
        Toast.makeText(this, "${v.text} clicked", Toast.LENGTH_SHORT).show()
    }

    @OnCheckChanged(value = [(R.id.cb_box)])
    private fun onCheckBox(v: View, checked: Boolean) {
        Toast.makeText(this, "On Check changed -> $checked", Toast.LENGTH_SHORT).show()
    }
}
