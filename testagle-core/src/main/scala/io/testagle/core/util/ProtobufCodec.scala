package io.testagle.util


import com.twitter.finagle.{Codec, CodecFactory}
import org.jboss.netty.handler.codec.protobuf.{ProtobufEncoder, ProtobufDecoder, ProtobufVarint32FrameDecoder, ProtobufVarint32LengthFieldPrepender}
import org.jboss.netty.channel.{Channels, ChannelPipelineFactory}
import net.sandrogrzicic.scalabuff.Message

/**
 * TODO: document this ASAP!!!
 */
class ProtobufCodec(val clientPrototype: Message, val serverPrototype: Message) extends CodecFactory[Message, Message] {

  val lengthPrepender = new ProtobufVarint32LengthFieldPrepender()
  val frameDecoder = new ProtobufVarint32FrameDecoder()

  def server = Function.const {
    new Codec[Message, Message] {
      def pipelineFactory = new ChannelPipelineFactory {
        def getPipeline = {
          val pipeline = Channels.pipeline()

          pipeline.addLast("messageEncoder",  new ProtobufVarint32LengthFieldPrepender())
          pipeline.addLast("messageDecoder", new ProtobufVarint32FrameDecoder())
          pipeline.addLast("decoder", new ProtobufDecoder(serverPrototype.getDefaultInstanceForType))
          pipeline.addLast("encoder", new ProtobufEncoder())

          pipeline
        }
      }
    }
  }

  def client = Function.const {
    new Codec[Message,Message] {
      def pipelineFactory = new ChannelPipelineFactory {
        def getPipeline = {
          val pipeline = Channels.pipeline()

          pipeline.addLast("messageDecoder", new ProtobufVarint32FrameDecoder())
          pipeline.addLast("messageEncoder",  new ProtobufVarint32LengthFieldPrepender())
          pipeline.addLast("encoder", new ProtobufEncoder())
          pipeline.addLast("decoder", new ProtobufDecoder(clientPrototype.getDefaultInstanceForType))

          pipeline
        }

      }
    }
  }


}

object  ProtobufCodec{
  /**
   * Passing two prototypes for case when server accepts some kind of requests and client another kind of responses
   */
  def apply(clientPrototype: Message, serverPrototype: Message) = new ProtobufCodec(clientPrototype, serverPrototype)

  /**
   * When client and server share common protocol
   */
  def apply(prototype: Message) = new ProtobufCodec(prototype, prototype)
}