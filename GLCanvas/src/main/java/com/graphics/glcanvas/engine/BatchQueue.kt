package com.graphics.glcanvas.engine
import com.graphics.glcanvas.engine.constants.Primitives
import com.graphics.glcanvas.engine.structures.Vertex
import java.util.PriorityQueue

class BatchQueue {

    companion object{
        const val ORDER=0
        const val UNORDER=1
    }
    private var mode= ORDER
    private val queue=PriorityQueue<BatchBucket>()
    private var priority=0

    fun setMode(mode:Int){
        this.mode=mode
    }

    fun getBucket(texture:Int,primitiveType: Primitives):BatchBucket?{
      for(it in queue){
          if(it.getPrimitiveType()==primitiveType&&it.getFirst().getTexture().getId()==texture)
              return it
      }
        return null
    }
    // first come first draw last come last draw
    // last elements will be drawn on top of first elements
    fun addVertex(vertex: Vertex,primitiveType: Primitives){
        // if the queue is empty let's add first item we don't care about priority
        if(queue.isEmpty()) {
            val bucket=BatchBucket(primitiveType)
                bucket.add(vertex)
                bucket.setPriority(priority)
                queue.add(bucket)
                priority++
        }else{
            // test if the last item matches this primitive
            val lastBucket=queue.last()
            if(mode== ORDER) {
                if (lastBucket.getPrimitiveType() == primitiveType && vertex.getTexture()
                        .getId() == lastBucket.getFirst().getTexture().getId()) {
                    lastBucket.add(vertex)
                } else {
                    priority++
                    // if not create a new bucket
                    // adds a draw call overhead but it sorts the elements in order
                    val bucket = BatchBucket(primitiveType)
                    bucket.add(vertex)
                    bucket.setPriority(priority)
                    queue.add(bucket)
                }
            }else {
                var bucket=getBucket(vertex.getTexture().getId(),primitiveType)
                if(bucket!=null){
                    bucket.add(vertex)
                }else{
                    priority++
                    // if not create a new bucket
                    // adds a draw call overhead but it sorts the elements in order
                    bucket = BatchBucket(primitiveType)
                    bucket.add(vertex)
                    bucket.setPriority(priority)
                    queue.add(bucket)
                }
            }
        }
    }

    fun reset(){
        queue.clear()
        priority=0
    }

   fun getBatchedQueue():PriorityQueue<BatchBucket>{
       return queue
   }

}