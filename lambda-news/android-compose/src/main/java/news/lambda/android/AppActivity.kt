package news.lambda.android

import android.content.Intent
import android.net.Uri as AndroidUri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import max.Navigator
import max.Uri
import news.lambda.android.ui.R
import news.lambda.android.ui.app.AppModel
import news.lambda.android.ui.app.AppScreen
import news.lambda.app.component.AppComponent.Msg
import news.lambda.app.component.AppComponent.Props
import oolong.render

class AppActivity : AppCompatActivity() {

    private val appModel: AppModel by lazy { AppModel() }
    private val navigator: Navigator by lazy { NavigatorApplication.of(this).navigator }

    private val render = render<Msg, Props> { props, dispatch ->
        appModel.props = props
        appModel.dispatch = dispatch
        Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_LambdaNews)
        super.onCreate(savedInstanceState)
        handleDeepLink(intent?.data, savedInstanceState == null)
        setContent { AppScreen(appModel, navigator) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent?.data)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        OolongApplication.of<Msg, Props>(this).setRender(render)
    }

    override fun onDetachedFromWindow() {
        OolongApplication.of<Msg, Props>(this).clearRender()
        super.onDetachedFromWindow()
    }

    override fun onBackPressed() {
        if (!navigator.pop()) {
            super.onBackPressed()
        }
    }

    private fun handleDeepLink(uri: AndroidUri?, replace: Boolean = false) {
        if (uri != null) {
            val route = Uri.parse(uri.path.toString())
            if (replace) navigator.set(route)
            else navigator.push(route)
        }
    }
}
