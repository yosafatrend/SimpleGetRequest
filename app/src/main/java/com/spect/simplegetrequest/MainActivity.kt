package com.spect.simplegetrequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    companion object{
        private val TAG = MainActivity::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getJobs()
    }

    private fun getJobs(){
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://job-api35.herokuapp.com/api/values/android"
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, responseBody: ByteArray) {
                progressBar.visibility = View.INVISIBLE
                val listQuote = ArrayList<String>()
                val result = String(responseBody)
                Log.d(TAG, "success " + result)
                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val name = jsonObject.getString("jobName")
                        val salary = jsonObject.getString("jobSalary")
                        val company = jsonObject.getString("jobCompany")
                        val desc = jsonObject.getString("jobDesc")
                        val location = jsonObject.getString("jobLocation")
                        listQuote.add("\n$name\n$salary\n$company - $location\n$desc")
                    }
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, listQuote)
                    listQuotes.adapter = adapter
                } catch (e: Exception) {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Errorfound : " + e.message)
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                progressBar.visibility = View.INVISIBLE

                val errorMessage = when (statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }

}