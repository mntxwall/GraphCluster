var clusterJs = (function (d3Draw) {

  var radius = 5;
  var size = 8;
  var color = d3.scaleOrdinal(d3.schemeCategory10);
  var wheight = window.innerHeight;
  var graphWidth =  window.innerWidth;
  var canvas = null;
  var context = null;
  var simulation = null;
  var transform = null;
  var width = 0, height = 0;
  var _graph = null;


  function Init(containerId, canvasId, graphData) {

    _graph = graphData;

    canvas = d3.select('#' + containerId).append('canvas')
        .attr('width', graphWidth + 'px')
        .attr('height', wheight + 'px')
        .attr('id', canvasId).node();
    context = canvas.getContext("2d");
        width = canvas.width;
        height = canvas.height;

    simulation = d3.forceSimulation();
    transform = d3.zoomIdentity;

    simulation.force("charge", d3.forceManyBody().strength(-300))
        .force("center", d3.forceCenter(width / 2, height / 2));


  }
  var draw = function draw(containerId, canvasId, graphData) {

    Init(containerId, canvasId, graphData);

    simulation
        .nodes(_graph.nodes)
        .on("tick", simulationUpdate);

    simulation.force("link", d3.forceLink().links(_graph.links).id(function(d) {
      return d.id;
    }).strength(0.07).distance(100));

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

  };

  function simulationUpdate() {
    context.save();

    context.clearRect(0, 0, graphWidth, height);
    context.translate(transform.x, transform.y);
    context.scale(transform.k, transform.k);

    _graph.links.forEach(drawLink);

    // Draw the nodes
    _graph.nodes.forEach(drawNode);

    context.restore();

  }

  function dragsubject() {
    var i,
        x = transform.invertX(d3.event.x),
        y = transform.invertY(d3.event.y),
        dx,
        dy;
    for (i = _graph.nodes.length - 1; i >= 0; --i) {
      node = _graph.nodes[i];
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

  function zoomed() {

    transform = d3.event.transform;
    simulationUpdate();
  }

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
  }

  return{
    draw: draw
  }
}(window));