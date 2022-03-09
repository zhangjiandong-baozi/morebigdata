/**
 * @author mangguodong
 * @create 2022-03-08
 */
object HelloScala {

  def main(args: Array[String]): Unit = {
    println("hello scala and 我们村我最帅")


    def f1(arr:String)={

      def fi():String = {
        "我们村我最帅"+arr
      }
      fi _
    }

    println(f1("!!!")())
  }

}
