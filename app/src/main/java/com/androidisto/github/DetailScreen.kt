package com.androidisto.github

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.androidisto.github.services.DownloadImageTask
import com.androidisto.github.services.JsonParserLocal
import org.apache.http.NameValuePair
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class DetailScreen : AppCompatActivity() {


    var jsonParser = JsonParserLocal()
    var GITHUB_FOLLOWERS_URL = "https://api.github.com/users/"

    lateinit var usernameTxt: TextView
    lateinit var userImage: ImageView
    lateinit var followersTable: TableLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_screen)

        usernameTxt = findViewById(R.id.user_name) as TextView;
        val userIdTxt = findViewById(R.id.user_id) as TextView;
        val useremailTxt = findViewById(R.id.user_email) as TextView;
        val userbioTxt = findViewById(R.id.user_bio) as TextView;
        val userPublicTxt = findViewById(R.id.user_repos) as TextView
        userImage = findViewById(R.id.avt_img) as ImageView
        followersTable = findViewById(R.id.tableFollowers) as TableLayout


        followersTable.isStretchAllColumns = true

        val b = intent.extras


        if (b != null) {


            userIdTxt.text = b.get("id") as String

            usernameTxt.text = b.get("username") as String



            if (b.get("bio").equals("null")) {

                userbioTxt.visibility = View.GONE

            } else {


                userbioTxt.text = "BIO: \n" + b.get("bio") as String

            }

            if (b.get("repos").equals("null")) {

                userPublicTxt.visibility = View.GONE

            } else {

                userPublicTxt.text = "Public repos: \n" + b.get("repos") as String
            }

            if (b.get("email").equals("null")) {

                useremailTxt.visibility = View.GONE

            } else {

                useremailTxt.text = "Email: \n" + b.get("email") as String


            }


            if (userImage != null) {
                DownloadImageTask().loadBitmap(b.get("avatar").toString(), userImage)

            }

            Followers().execute()


        }


    }

    // GetFollowers
    internal inner class Followers : AsyncTask<String, String, String>() {
        val progress = findViewById(R.id.progress) as ProgressBar
        var url = GITHUB_FOLLOWERS_URL + usernameTxt.text + "/followers"


        override fun onPreExecute() {
            super.onPreExecute()
            progress.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg args: String): String? {
            // TODO Auto-generated method stub
            // Check for success tag


            try {

                val params = ArrayList<NameValuePair>()

                jsonParser.updateAndroidSecurityProvider(applicationContext)
                val json = jsonParser.makeHttpRequest(url, "GET",
                        params)

                Log.d("Login attempt", json.toString() + "")



                return json.toString()


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null

        }

        override fun onPostExecute(response: String?) {
            // dismiss the dialog
            progress.visibility = View.GONE


            // function to parse and populate the tablelayout
            ParseJson(response.toString())


        }

    }


    private fun ParseJson(json: String) {

        var list: ArrayList<HashMap<String, String>> = arrayListOf()
        var jsonArray = JSONArray(json)
        for (i in 0..(jsonArray.length() - 1)) {
            val follower = jsonArray.getJSONObject(i)


            val row = TableRow(this)

            val followersList = layoutInflater.inflate(R.layout.followers_list_view, null)
            val userImage = followersList.findViewById(R.id.avt_img) as ImageView
            val userName = followersList.findViewById(R.id.user_name) as TextView



            if (userImage != null) {

                DownloadImageTask().loadBitmap(follower.getString("avatar_url"), userImage)  // image caching and downloading class
            }
            userName.text = follower.getString("login")

            row.addView(followersList)
            followersTable.addView(row)

        }

    }
}
