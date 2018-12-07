package models

import java.util

import javax.inject.Inject
import org.jgrapht.Graphs
import org.jgrapht.graph.{DefaultEdge, DefaultUndirectedGraph}

import collection.JavaConverters._
import scala.collection.mutable

case class ClusterHash(clique:Int, clusterSet:Set[Set[String]])

class CPMRepository @Inject()(graphRepository: GraphRepository) {

  private val udirectedGraph: DefaultUndirectedGraph[String, DefaultEdge] =
    new DefaultUndirectedGraph[String, DefaultEdge](classOf[DefaultEdge])

  private var K: Int = 3
/*
  private var vertexSet: Set[String] = Set[String]()
  private var edgeSet: Set[Set[String]] = Set[Set[String]]()

  def this(vertexSet: Set[String], edgeSet: Set[Set[String]]){

    this()
    this.vertexSet = vertexSet
    this.edgeSet = edgeSet
  }*/

 // Graphs.addAllVertices(udirectedGraph, new util.ArrayList[String](util.Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9")))

  def CreateGraph(): DefaultUndirectedGraph[String, DefaultEdge] = {

    Graphs.addAllVertices(udirectedGraph, new util.ArrayList[String](util.Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9")))

    udirectedGraph.addEdge("1", "2")
    udirectedGraph.addEdge("1", "3")
    udirectedGraph.addEdge("1", "4")
    udirectedGraph.addEdge("2", "3")
    udirectedGraph.addEdge("3", "4")
    udirectedGraph.addEdge("4", "5")
    udirectedGraph.addEdge("4", "6")
    udirectedGraph.addEdge("5", "6")
    udirectedGraph.addEdge("5", "7")
    udirectedGraph.addEdge("5", "8")
    udirectedGraph.addEdge("6", "7")
    udirectedGraph.addEdge("6", "8")
    udirectedGraph.addEdge("7", "8")
    udirectedGraph.addEdge("7", "9")

    udirectedGraph
  }

  def CreateGraph(tableName: String) = {
    //val aa =
    Graphs.addAllVertices(udirectedGraph, graphRepository.getVertex(tableName).asJavaCollection)

    graphRepository.getEdges(tableName).foreach(x => udirectedGraph.addEdge(x(0), x(1)))
    //udirectedGraph.

    udirectedGraph
  }

  def getVertexSet() = {

    val edgeVertexSet = mutable.Set[Set[String]]()

    // CreateGraph
    udirectedGraph.edgeSet().forEach(x => {
      val edgeVertextOne = udirectedGraph.getEdgeSource(x)
      val edgeVertextTwo = udirectedGraph.getEdgeTarget(x)
      val setVertex: Set[String] = Set(edgeVertextOne, edgeVertextTwo)
      //cliqueHashMap.apply(2) += setVertex
      edgeVertexSet += setVertex

    })

    edgeVertexSet

  }

  def getCliques2() = {


    //val edgeVertexSet = getReadableEdge

    var cliqueResult = getReadableEdge()
    val cliqueHashMap = mutable.HashMap[Int, Set[Set[String]]]()

    while (cliqueResult.nonEmpty){

      cliqueResult = findClique4(K, cliqueResult)

      if (!cliqueHashMap.contains(K))
        cliqueHashMap.put(K, cliqueResult)
      else
        cliqueHashMap.update(K, cliqueResult)

      println(s"The $K-clique is ${cliqueHashMap.apply(K)}")
      K += 1
    }

    cliqueHashMap
    //println(cliqueHashMap)

  }

  def getCliques() = {


    val edgeVertexSet = getVertexSet

    var cliqueResult = edgeVertexSet
    val cliqueHashMap = mutable.HashMap[Int, mutable.Set[Set[String]]]()

    while (cliqueResult.nonEmpty){

      cliqueResult = findClique(K, cliqueResult)

      if (!cliqueHashMap.contains(K))
        cliqueHashMap.put(K, cliqueResult)
      else
        cliqueHashMap.update(K, cliqueResult)

      println(s"The $K-clique is ${cliqueHashMap.apply(K)}")
      K += 1
    }

    cliqueHashMap

  }

  def getReadableEdge() = {

    udirectedGraph.edgeSet().asScala.toSet.map{x: DefaultEdge =>
      Set(udirectedGraph.getEdgeSource(x), udirectedGraph.getEdgeTarget(x))
      //cliqueHashMap.apply(2) += setVertex
    }
  }


  def findCPMCluster2(cliqueHashMap: mutable.HashMap[Int, Set[Set[String]]]) ={

    cliqueHashMap.map{x =>

      if(x._2.size > 0){

        //var aaa = x._2.toSeq(0)
        var searchSet = x._2

        x._2.foreach{xx =>

          val mergeSet = mutable.Set[Set[String]]()
          //println("xx is " + xx)
          //println("a2 is " + a2)
          searchSet =  searchSet.map{ y =>

            val t =  y.intersect(xx)
            if (t.size >= x._1 -1 && t.size < x._1){

              val m = y ++ xx
              mergeSet.add(m)
              m

            }
            else
              y
          }
          if (mergeSet.size >= 2){
            searchSet = searchSet -- mergeSet
            searchSet = searchSet ++ Set(mergeSet.flatten.toSet[String])
            //println("a3 flattern is " + a3.flatten.toSet[String])
          }

        }
        //println("result is " + searchSet.getClass)
        ClusterHash(x._1, searchSet)
        //Map(x._1 -> searchSet)
      }
      else
        ClusterHash(x._1, Set[Set[String]]())
        //Map(x._1 -> Set[Set[String]]())
    }
  }

  def findCPMCluster(cliqueHashMap: mutable.HashMap[Int, mutable.Set[Set[String]]]) = {

    val clusterHashMap = mutable.HashMap[Int, mutable.ListBuffer[mutable.Set[String]]]()

    cliqueHashMap.keysIterator.foreach(cliqueIndex => {

      println("cliqueIndex is " + cliqueIndex)
      //根据k子图，添加空白List有待添加k子图的分组
      clusterHashMap.put(cliqueIndex, mutable.ListBuffer[mutable.Set[String]]())

      /*
      *算法描述：

      根据cliqueHashMap中的结构，构建clusterHashMap。
      clusterHashMap与cliqueHashMap中不一样的地方在于：
      clusterHashMap用来保存k子图下的分组结果。
      cliqueHashMpa是保存k子图的结果。

      cliqueHashMap中k子图记为m，其中m(i)表示k子图下的第i个元素。
      如果m(i)不存在于clusterHashMap中,将m(i)加入到clusterHashMap中记为c(i)。

      做c(i)与m(i)的交集得出结果s(i,i)，如果s(i,i)集合中元素的个数大于等于k-1，
      那么c(i)和m(i)可以组成一个分组。

      在做分组的过程中会出现c(i)与c(j)和m(i)的交集s(i,i),s(j,i)都会大于等于k-1
      那么说明c(i)和c(j)是连通的，那么就要合并c(i)和c(j)
      *
      *
      *
      * */
      cliqueHashMap.apply(cliqueIndex).foreach(cliqueSetA => {

        println("cliqueSetA is " + cliqueSetA)
        val tmpCliqueSetAtoMutable = collection.mutable.Set(cliqueSetA.toArray:_*)
        val clusterSet = clusterHashMap.apply(cliqueIndex)

        println("ClusterSet is " + clusterSet)
        var isMerge: Int = 0

        if (clusterSet.isEmpty){
          clusterSet.append(tmpCliqueSetAtoMutable)
        }
        else{
          //用来保存处理过的set，如果还有相同的边，要进行Set合并
          var maxSet = mutable.Set[String]()
          var listIndex: Int = 0



          clusterSet.foreach(clusterIndexSet => {

            if (clusterIndexSet.intersect(cliqueSetA).size >= cliqueIndex - 1){

              if (maxSet.isEmpty){
                listIndex = clusterSet.indexOf(clusterIndexSet)
                clusterIndexSet ++= tmpCliqueSetAtoMutable
                isMerge = 1
                maxSet = clusterIndexSet
              }
              else{
                clusterSet(listIndex) ++= tmpCliqueSetAtoMutable
                clusterSet -= clusterIndexSet
              }
            }
          })
          if (isMerge == 0){
            clusterSet.append(tmpCliqueSetAtoMutable)
          }

        }

      })
    })

    clusterHashMap
  }
  private def findClique4(kIndex: Int, kClique: Set[Set[String]]) = {

    var cliqueSet = Set[Set[String]]()
    val checkConnectedHash = mutable.HashMap[Set[String], Int]()
    kClique.flatMap{cliqueSetIndex =>

      //println("cliqueSetIndex is " + cliqueSetIndex)
      checkConnectedHash.clear()

      cliqueSetIndex.flatMap{cliqueVertextIndex =>
        if(udirectedGraph.degreeOf(cliqueVertextIndex) >= kIndex - 1){
          //println("cliqueVertexIndex is " + cliqueVertextIndex)
          Graphs.neighborSetOf(udirectedGraph, cliqueVertextIndex).asScala
            .map{ nvSetEle:String =>
              //           Set[String](nvSetEle)
              if (!cliqueSetIndex.contains(nvSetEle) && udirectedGraph.degreeOf(nvSetEle) >= kIndex - 1){
                val newvla: Set[String] = cliqueSetIndex + nvSetEle
                if (!cliqueSet.contains(newvla)){
                  if(!checkConnectedHash.contains(newvla)){
                    checkConnectedHash.put(newvla, 0)
                    Set[String]("")
                  }
                  if (udirectedGraph.containsEdge(cliqueVertextIndex, nvSetEle)){
                    checkConnectedHash.apply(newvla) += 1
                    if (checkConnectedHash.apply(newvla) >= kIndex - 1)
                    {
                      cliqueSet += newvla
                      newvla
                    }else Set[String]("")
                  }else Set[String]("")
                }else Set[String]("")
              }else Set[String]("")
            }.filter(x => x.size > 1)
        }
        else
          Set[Set[String]]()
      }.filter(x => x.size > 1)
    }


  }

  private def findClique3(kIndex: Int, kClique: Set[Set[String]]) = {

    var cliqueSet = Set[Set[String]]()
    val checkConnectedHash = mutable.HashMap[Set[String], Int]()
    val tttt = kClique.map{cliqueSetIndex =>

      println("cliqueSetIndex is " + cliqueSetIndex)
      checkConnectedHash.clear()

      val ttt = cliqueSetIndex.flatMap{cliqueVertextIndex =>
        if(udirectedGraph.degreeOf(cliqueVertextIndex) >= kIndex - 1){
          println("cliqueVertexIndex is " + cliqueVertextIndex)
          val aaa = Graphs.neighborSetOf(udirectedGraph, cliqueVertextIndex).asScala
          .map{ nvSetEle:String =>
            //           Set[String](nvSetEle)
            if (!cliqueSetIndex.contains(nvSetEle) && udirectedGraph.degreeOf(nvSetEle) >= kIndex - 1){
              val newvla: Set[String] = cliqueSetIndex + nvSetEle
              if (!cliqueSet.contains(newvla)){
                if(!checkConnectedHash.contains(newvla)){
                  checkConnectedHash.put(newvla, 0)
                  Set[String]("")
                }
                if (udirectedGraph.containsEdge(cliqueVertextIndex, nvSetEle)){
                  checkConnectedHash.apply(newvla) += 1
                  if (checkConnectedHash.apply(newvla) >= kIndex - 1)
                  {
                    cliqueSet += newvla
                    newvla
                  }else Set[String]("")
                }else Set[String]("")
              }else Set[String]("")
            }else Set[String]("")
          }

          val bbb = aaa.filter(x => x.size > 1)
          //val bbb = aaa.flatten
          //val bbb = aaa.foreach(x => println(x.getClass))
          println("bbb is " + bbb)
          aaa
        }
        else
          Set[Set[String]]()
      }

      val ccc = ttt.filter(x => x.size > 1)
      println("ccc is " + ccc)

      ccc
      //println(mmm)
    }

    println(tttt.flatten)

  }

  private def findClique2(kIndex: Int, kClique: mutable.Set[Set[String]]) = {

    //Storage the result clique
    val cliqueSet = mutable.Set[Set[String]]()

    //Storage vertex counting result
    val checkConnectedHash = mutable.HashMap[Set[String], Int]()

    kClique.foreach{cliqueSetIndex =>

      println("cliqueSetIndex is " + cliqueSetIndex)
      val zzz = cliqueSetIndex.map{cliqueVertextIndex =>
        if(udirectedGraph.degreeOf(cliqueVertextIndex) >= kIndex - 1){

          //println("cliqueVertexIndex is " + cliqueVertextIndex)
          val aaa = Graphs.neighborSetOf(udirectedGraph, cliqueVertextIndex).asScala

          val bbb = aaa.flatMap{x =>

            if (!cliqueSetIndex.contains(x) && udirectedGraph.degreeOf(x) >= kIndex - 1){

              val newvla: Set[String] = cliqueSetIndex + x
              if (!cliqueSet.contains(newvla)) {
                if(!checkConnectedHash.contains(newvla)){
                  checkConnectedHash.put(newvla, 0)
                }
                if (udirectedGraph.containsEdge(cliqueVertextIndex, x)){
                  checkConnectedHash.apply(newvla) += 1
                  if (checkConnectedHash.apply(newvla) >= kIndex - 1){

                    newvla
                    //cliqueSet += newvla
                  }
                  else{
                    Set[String]()
                  }
                }
                else {
                  Set[String]()
                }
                //newvla
              }
              else {
                Set[String]()
              }
            }
            else {
              Set[String]()
            }

          }

          println("bbb is " + bbb)

          aaa.map{ nvSetEle:String =>
 //           Set[String](nvSetEle)
            if (!cliqueSetIndex.contains(nvSetEle) && udirectedGraph.degreeOf(nvSetEle) >= kIndex - 1){
              val newvla: Set[String] = cliqueSetIndex + nvSetEle
              if (!cliqueSet.contains(newvla)){
                if(!checkConnectedHash.contains(newvla)){
                  checkConnectedHash.put(newvla, 0)
                }
                if (udirectedGraph.containsEdge(cliqueVertextIndex, nvSetEle)){
                  checkConnectedHash.apply(newvla) += 1
                  if (checkConnectedHash.apply(newvla) >= kIndex - 1){
                    newvla
                    //cliqueSet += newvla
                  }
                }
              }
              else{
                Set[String]()
              }
            }
            else
              Set[String]()

          }
          //println("ttt is " + tt)
          //tt
        }
        else
          Set[Set[String]]()
      }
      checkConnectedHash.clear()
      //println(zzz)
    }
  }

  private def findClique(kIndex: Int, kClique: mutable.Set[Set[String]]): mutable.Set[Set[String]] = {

    //Storage the result clique
    val cliqueSet = mutable.Set[Set[String]]()

    //Storage vertex counting result
    val checkConnectedHash = mutable.HashMap[Set[String], Int]()

    if (kClique.nonEmpty)
    {
      kClique.foreach(cliqueSetIndex => {

        // println("edge is " + cliqueSetIndex)
        cliqueSetIndex.foreach(cliqueVertextIndex => {
          //   println("edge vertext is " + cliqueVertextIndex)

          if(udirectedGraph.degreeOf(cliqueVertextIndex) >= kIndex - 1){
            Graphs.neighborSetOf(udirectedGraph, cliqueVertextIndex).forEach( nvSetEle => {

              //    println("vertext is " + nvSetEle + " and the degree is " + udirectedGraph.degreeOf(nvSetEle))
              if (!cliqueSetIndex.contains(nvSetEle) && udirectedGraph.degreeOf(nvSetEle) >= kIndex - 1) {
                //Graphs.getOppositeVertex(udirectedGraph, x, edgeVertext)
                val newvla: Set[String] = cliqueSetIndex + nvSetEle

                //     println("Checking vertexs is " + newvla)

                if (!cliqueSet.contains(newvla)){
                  if(!checkConnectedHash.contains(newvla)){
                    checkConnectedHash.put(newvla, 0)
                  }
                  if (udirectedGraph.containsEdge(cliqueVertextIndex, nvSetEle)){
                    checkConnectedHash.apply(newvla) += 1
                    //       print(newvla)
                    //      println(" value is " + checkConnectedHash.apply(newvla).toString)

                    if (checkConnectedHash.apply(newvla) >= kIndex - 1){
                      cliqueSet += newvla
                    }
                  }
                }
              }
            })

          }

        })
        checkConnectedHash.clear()
      })

    }

    cliqueSet
  }
}
