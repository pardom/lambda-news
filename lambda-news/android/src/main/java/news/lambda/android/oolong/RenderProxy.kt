package news.lambda.android.oolong

import oolong.Dispatch
import oolong.Render

class RenderProxy<Msg, Props> : Render<Msg, Props> {

    private var props: Props? = null
    private var dispatch: Dispatch<Msg>? = null
    private var delegate: Render<Msg, Props>? = null

    override fun invoke(props: Props, dispatch: Dispatch<Msg>): Any? {
        this.props = props
        this.dispatch = dispatch
        return delegate?.invoke(props, dispatch)
    }

    fun dispatch(msg: Msg) {
        dispatch?.invoke(msg)
    }

    fun setDelegate(delegate: Render<Msg, Props>) {
        this.delegate = delegate
        props?.let { props ->
            dispatch?.let { dispatch ->
                delegate.invoke(props, dispatch)
            }
        }
    }

    fun clearDelegate() {
        delegate = null
    }

}
