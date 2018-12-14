(function (wei, clusterJs) {

  wei.addEventListener('load', function (evt) {
    document.getElementById('node-category').addEventListener('change', function(evt) {

      console.log(evt.target.value)

      console.log(evt.target.value);


      console.log(graph.group);

      if (document.getElementById('initCanvas') !== null){
        //var initCanvas = d3.select('#initCanvas').node();
        //initCanvas.remove();
      }
      if ( document.getElementById('filterCanvas') !== null) {
        //var filterCanvas = d3.select('#filterCanvas').node();
        //filterCanvas.remove()
      }

      if (evt.target.value === "0"){
        //filterGraphData.nodes = graph.nodes;
        //filterGraphData.links = graph.links;
        console.log("aabbcc");

        clusterJs.draw('graphDiv', 'initCanvas', graph)
      }
      else {
        var filterGraphData = {'nodes':[], 'links': []};

        graph.group.forEach(function (e){

          if("" + e.clique === evt.target.value){

            console.log(e.cluster);
            filterGraphData.nodes = e.nodes;
            filterGraphData.links = e.links
          }
        });

        //clusterJs.draw('graphDiv', 'filterCanvas', filterGraphData);
        clusterJs.draw('graphDiv', 'initCanvas', filterGraphData);

      }
      console.log(filterGraphData);

    });

    document.getElementById('reset-btn').addEventListener('click', function (ev){
      if (document.getElementById('initCanvas') !== null){
        var initCanvas = d3.select('#initCanvas').node();
        initCanvas.remove();
      }
      if ( document.getElementById('filterCanvas') !== null) {
        var filterCanvas = d3.select('#filterCanvas').node();
        filterCanvas.remove()
      }

      clusterJs.draw('graphDiv', 'initCanvas', graph)
      document.getElementById('node-category').value = "0"

    })
  })
})(window, clusterJs);