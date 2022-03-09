package com.baowen.base

import scala.Boolean

/**
 * @author mangguodong
 * @create 2022-03-09
 */
object MyWhile {

  def main(args: Array[String]): Unit = {


    var i = 10;
    while (i>=1){
      println(i)
      i-=1
    }

    println("===========")
    //利用控制抽象自定义while语法
    //使用柯里化
    def Mywhile1(codition: =>Boolean)(op: =>Unit):Unit={
      if(codition){
        op
        Mywhile1(codition)(op)
      }
    }

     i = 10;
    Mywhile1 (i>=1){
      println(i)
      i-=1
    }

    //使用闭包写while
    println("===========")
    def Mywhile2(condition: =>Boolean):(=>Unit)=>Unit ={

      def Doloop(op: =>Unit):Unit={
        if(condition){
          op
          Mywhile2(condition)(op)
        }
      }
      Doloop _
    }


    i = 10;
    Mywhile2 (i>=1){
      println(i)
      i-=1
    }

    //闭包使用lamada的匿名函数形式
    def Mywhile3(condition: =>Boolean):(=>Unit)=>Unit ={
      op=>{
        if(condition){
          op
          Mywhile3(condition)(op)
        }
      }
    }
    println("===========")
    i = 10;
    Mywhile3 (i>=1)({
      println(i)
      i-=1
    })







  }

}
