package news.lambda.android.util

import max.Navigator
import news.lambda.app.component.AppComponent.Msg
import news.lambda.app.component.AppComponent.Route

fun Navigator.Request.createSetRouteMsg(nextRoute: Route) =
    Msg.SetRoute(
        nextRoute,
        route.toString(),
        prevRoute.toString(),
        direction.toDomain()
    )

fun Navigator.Direction.toDomain() =
    when (this) {
        Navigator.Direction.FORWARD -> Route.Direction.FORWARD
        Navigator.Direction.BACKWARD -> Route.Direction.BACKWARD
        Navigator.Direction.REPLACE -> Route.Direction.REPLACE
    }
