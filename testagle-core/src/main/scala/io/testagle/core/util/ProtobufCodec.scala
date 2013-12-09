package io.testagle.util


import com.twitter.finagle.{Codec, CodecFactory}
import org.jboss.netty.handler.codec.protobuf.{ProtobufEncoder, ProtobufDecoder, ProtobufVarint32FrameDecoder, ProtobufVarint32LengthFieldPrepender}
import org.jboss.netty.channel.{Channels, ChannelPipelineFactory}
import io.testagle.core.TestagleProtocol

class ProtobufCodec(val clientPrototype: TestagleProtocol, val serverPrototype: TestagleProtocol) extends CodecFactory[TestagleProtocol, TestagleProtocol] {

  val lengthPrepender = new ProtobufVarint32LengthFieldPrepender()
  val frameDecoder = new ProtobufVarint32FrameDecoder()

  def server = Function.const {
    new Codec[TestagleProtocol, TestagleProtocol] {
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
    new Codec[TestagleProtocol,TestagleProtocol] {
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
   * When client and server share common protocol
   */
  def apply(prototype: TestagleProtocol) = new ProtobufCodec(prototype, prototype)
}