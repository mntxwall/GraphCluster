(function (d3Draw, d3, clusterJs, graph) {

  d3Draw.addEventListener('load', function (evt) {


    clusterJs.draw('graphDiv', 'initCanvas', graph)
    /*
    var radius = 5;
    var size = 8;
    var color = d3.scaleOrdinal(d3.schemeCategory10);
    var wheight = window.innerHeight;
    var graphWidth =  window.innerWidth;
    var canvas = d3.select('#graphDiv').append('canvas')
      .attr('width', graphWidth + 'px')
      .attr('height', wheight + 'px')
      .attr('id', "initCanvas").node();


    var  context = canvas.getContext("2d"),
      width = canvas.width,
      height = canvas.height;
    var simulation = d3.forceSimulation()


      .force("charge", d3.forceManyBody().strength(-300))
      .force("center", d3.forceCenter(width / 2, height / 2));
    var transform = d3.zoomIdentity;

   // d3.json("/d3/data").then(function(graph) {
      //console.log(graph);
      //add

      simulation
        .nodes(graph.nodes)
        .on("tick", simulationUpdate);

      simulation.force("link", d3.forceLink().links(graph.links).id(function(d) {
        return d.id;
      }).strength(0.07).distance(100));

      //simulation.gravity(0.05)

      var zoom = d3.zoom().scaleExtent([1/5, 8])
        .on("zoom", zoomed);

      d3.select(canvas)
        .call(d3.drag()
          .container(canvas)
          .subject(dragsubject)
          .on("start", dragstarted)
          .on("drag", dragged)
          .on("end", dragended))
        .call(zoom);
      //  .call(d3.zoom().scaleExtent([1, 8]).on("zoom", zoomed));



      function zoomed() {

        transform = d3.event.transform;
        simulationUpdate();
      }

      function simulationUpdate(){
        context.save();

        context.clearRect(0, 0, graphWidth, height);
        context.translate(transform.x, transform.y);
        context.scale(transform.k, transform.k);

        graph.links.forEach(drawLink);

        // Draw the nodes
        graph.nodes.forEach(drawNode);

        context.restore();
      }

      function ticked() {


        context.clearRect(0, 0, width, height);

        context.beginPath();
        graph.links.forEach(drawLink);
        context.strokeStyle = "#aaa";
        context.stroke();
        graph.nodes.forEach(drawNode);

      }

      function dragsubject() {
        var i,
          x = transform.invertX(d3.event.x),
          y = transform.invertY(d3.event.y),
          dx,
          dy;
        for (i = graph.nodes.length - 1; i >= 0; --i) {
          node = graph.nodes[i];
          dx = x - node.x;
          dy = y - node.y;

          if (dx * dx + dy * dy < radius * radius) {

            node.x =  transform.applyX(node.x);
            node.y = transform.applyY(node.y);

            return node;
          }
        }
        //return simulation.find(d3.event.x, d3.event.y);
      }

  //  });
    function dragstarted() {
      if (!d3.event.active) simulation.alphaTarget(0.3).restart();
      d3.event.subject.fx = transform.invertX(d3.event.x);
      d3.event.subject.fy = transform.invertY(d3.event.y);
      //d3.event.subject.fx = d3.event.subject.x;
      //d3.event.subject.fy = d3.event.subject.y;
    }

    function dragged() {
      d3.event.subject.fx = transform.invertX(d3.event.x);
      d3.event.subject.fy = transform.invertY(d3.event.y);
      //d3.event.subject.fx = d3.event.x;
      //d3.event.subject.fy = d3.event.y;
    }

    function dragended() {
      if (!d3.event.active) simulation.alphaTarget(0);
      d3.event.subject.fx = null;
      d3.event.subject.fy = null;
    }


    function drawLink(d) {
      context.beginPath();
      context.moveTo(d.source.x, d.source.y);
      context.lineTo(d.target.x, d.target.y);
      context.strokeStyle = "#aaa";
      context.stroke();
    }


    function drawNode(d) {

      context.beginPath();
      context.fillStyle = color(d.group % 10);
      //context.moveTo(d.x, d.y);
      context.arc(d.x, d.y, size, 0, 2 * Math.PI);
      context.fillText(d.id, d.x+10, d.y+3);

      //console.log();
      //context.strokeStyle = color(d.group);
      context.fill();
      context.stroke();
    }*/

  })

})(window, d3, clusterJs, graph);