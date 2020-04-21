package news.lambda.android

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import max.Navigator
import news.lambda.android.oolong.RenderProxy
import news.lambda.android.util.createSetRouteMsg
import news.lambda.app.component.AppComponent.Msg
import news.lambda.app.component.AppComponent.Props
import news.lambda.app.component.AppComponent.Route
import news.lambda.model.ItemId
import oolong.Oolong
import oolong.Render

class App : Application(), OolongApplication<Msg, Props>, NavigatorApplication {

    private val renderProxy = RenderProxy<Msg, Props>()

    override val navigator: Navigator = Navigator(Routes.itemList()) {
        path("/items") {
            route("") { request ->
                val setRouteMsg = request.createSetRouteMsg(Route.ItemList)
                renderProxy.dispatch(setRouteMsg)
            }
            route("/:$ITEM_ID") { request ->
                val itemId = ItemId(request.params.getValue(ITEM_ID).toLong())
                val setRouteMsg = request.createSetRouteMsg(Route.ItemDetail(itemId))
                renderProxy.dispatch(setRouteMsg)
            }
        }
        path("/users") {
            route("/:$USER_ID") {
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(Graph.networkFlipperPlugin)
            client.start()
        }

        Oolong.runtime(
            Graph.init(Route.ItemList),
            Graph.update,
            Graph.view,
            renderProxy
        )
    }

    override fun setRender(render: Render<Msg, Props>) {
        renderProxy.setDelegate(render)
    }

    override fun clearRender() {
        renderProxy.clearDelegate()
    }

    companion object {
        const val ITEM_ID = "itemId"
        const val USER_ID = "userId"
    }
}
