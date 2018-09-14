package com.androidisto.github

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.androidisto.github.services.DownloadImageTask
import com.androidisto.github.services.JsonParserLocal
import org.apache.http.NameValuePair
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {


    val b = Bundle()
    var jsonParser = JsonParserLocal() // parser class to parser data
    var GITHUB_URL = "https://api.github.com/users/"  // github base api url
    var username = ""
    lateinit var userLayout: RelativeLayout
    lateinit var usernameTxt: TextView
    lateinit var userimageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        val searchTXT = findViewById(R.id.search) as EditText;
        val searchBtn = findViewById(R.id.search_btn) as Button;
        usernameTxt = findViewById(R.id.user_name) as TextView;
        userimageView = findViewById(R.id.avt_img) as ImageView;
        userLayout = findViewById(R.id.layout) as RelativeLayout



        userLayout.setOnClickListener {

            val intent = Intent(this, DetailScreen::class.java)
            intent.putExtras(b)
            startActivity(intent)


        }




        searchTXT.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                username = searchTXT.text.toString()
                if (TextUtils.isEmpty(username) || username.indexOf(" ") >= 0) {

                    searchTXT.setError("Please enter a valid username !!");
                } else {


                    SearchUser().execute();


                }

                true
            } else {
                false
            }
        }


        searchBtn.setOnClickListener {


            username = searchTXT.text.toString()


            if (TextUtils.isEmpty(username) || username.indexOf(" ") >= 0) {

                searchTXT.setError("Please enter a valid username !!");
            } else {


                SearchUser().execute();


            }

        }


    }


    // GET User Data
    internal inner class SearchUser : AsyncTask<String, String, String>() {
        val progress = findViewById(R.id.progress) as ProgressBar


        override fun onPreExecute() {
            super.onPreExecute()
            userLayout.visibility = View.GONE

            progress.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg args: String): String? {
            // TODO Auto-generated method stub
            // Check for success tag
            val success: Int

            try {

                val params = ArrayList<NameValuePair>()



                jsonParser.updateAndroidSecurityProvider(applicationContext)
                val json = jsonParser.makeHttpRequest(GITHUB_URL + username, "GET",
                        params)

                Log.d("response", json.toString() + "")



                return json.toString()


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null

        }

        override fun onPostExecute(response: String?) {
            // dismiss the dialog
            progress.visibility = View.GONE

            var responseObj = JSONObject(response)
            if (responseObj.has("message")) {

                Toast.makeText(applicationContext, responseObj.getString("message"), Toast.LENGTH_LONG).show()

            } else {


                userLayout.visibility = View.VISIBLE





                usernameTxt.text = responseObj.getString("login")

                b.putString("username", responseObj.getString("login"))
                b.putString("id", responseObj.getString("id"))
                b.putString("bio", responseObj.getString("bio"))
                b.putString("email", responseObj.getString("email"))
                b.putString("company", responseObj.getString("company"))
                b.putString("repos", responseObj.getString("public_repos"))
                b.putString("avatar", responseObj.getString("avatar_url"))

                if (userimageView != null) {
                    DownloadImageTask().loadBitmap(responseObj.getString("avatar_url"), userimageView)

                }


            }
        }

    }


}
