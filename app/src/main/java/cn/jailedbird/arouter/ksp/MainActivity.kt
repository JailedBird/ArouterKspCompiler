package cn.jailedbird.arouter.ksp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired

class MainActivity : AppCompatActivity() {

    @Autowired
    var list = mutableListOf<String>()

    @Autowired
    var arrayList = arrayListOf<String>()

    @Autowired
    var array = LinkedHashMap<String, ArrayList<Int>>()

    @Autowired
    var hasSet = HashSet<LinkedHashMap<String, ArrayList<Int>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}