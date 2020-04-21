package news.lambda.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import max.Navigator
import news.lambda.android.ui.AppModel
import news.lambda.android.ui.AppScreen
import news.lambda.android.ui.R
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
        setContent { AppScreen(appModel, navigator) }
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

}