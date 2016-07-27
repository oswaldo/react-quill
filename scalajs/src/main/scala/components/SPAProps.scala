package components

import diode.react.ModelProxy
import diode.ModelR
import diode.Dispatcher

case class SPAProps(token: ModelR[_, Option[String]], dispatch: Dispatcher)
