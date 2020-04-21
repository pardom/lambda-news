package news.lambda.android

import android.content.Context
import oolong.Render

interface OolongApplication<Msg, Props> {

    fun setRender(render: Render<Msg, Props>)

    fun clearRender()

    companion object {

        fun <Msg, Props> of(context: Context): OolongApplication<Msg, Props> =
            context.applicationContext as OolongApplication<Msg, Props>
    }
}
