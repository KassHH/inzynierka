package model.messaging

trait Message {
	val id: Long
	def getId: Long = id
}