package cn.codebro.j_anime_android.core.event

class EventBus {
    private val subscribers: MutableMap<String, MutableList<Subscriber<Event>>> = mutableMapOf()
    fun subscribe(eventName: String, subscriber: Subscriber<Event>) {
        if (subscribers[eventName] == null) {
            subscribers[eventName] = mutableListOf()
        }
        subscribers[eventName]?.add(subscriber)
    }

    fun postEvent(event: Event) {
        subscribers[event.getName()]?.forEach {
            it.onEventPost(event)
        }
    }
}

interface Subscriber<out T : Event> {
    fun onEventPost(event: Event)
}

interface Event {
    fun getName(): String
}

