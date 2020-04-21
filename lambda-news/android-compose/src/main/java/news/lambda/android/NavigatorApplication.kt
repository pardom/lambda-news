package news.lambda.android

import android.content.Context
import max.Navigator

interface NavigatorApplication {

    val navigator: Navigator

    companion object {

        fun of(context: Context): NavigatorApplication =
            context.applicationContext as NavigatorApplication
    }

}
