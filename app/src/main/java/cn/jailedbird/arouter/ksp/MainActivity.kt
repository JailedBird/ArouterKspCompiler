package cn.jailedbird.arouter.ksp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired

class MainActivity : AppCompatActivity() {

    @Autowired
    var list = mutableListOf<String>()
    @Autowired
    var arrayList: ArrayList<String> = arrayListOf<String>()

    @Autowired
    var array: LinkedHashMap<String, ArrayList<Int>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}