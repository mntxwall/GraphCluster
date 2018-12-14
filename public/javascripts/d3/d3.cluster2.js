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
  var _linkLayer, _nodeLayer, _stage;


  function Init(containerId, canvasId, graphData) {

    _graph = graphData;

    //console.log(_stage);
    if(typeof _stage !== "undefined"){
      _stage.clear();
      _stage.destroy()
    }

    _stage = new Konva.Stage({
      container: containerId,   // id of container <div>
      width: graphWidth,
      height: wheight
    });



    //_stage.clear();



    _linkLayer = new Konva.Layer();
    _stage.add(_linkLayer);
    _nodeLayer = new Konva.Layer();
    _stage.add(_nodeLayer);


   /* canvas = d3.select('#' + containerId).append('canvas')
        .attr('width', graphWidth + 'px')
        .attr('height', wheight + 'px')
        .attr('id', canvasId).node();
    context = canvas.getContext("2d");

    width = canvas.width;
    height = canvas.height;*/

    width = _stage.attrs.width;
        height = _stage.attrs.height;

    simulation = d3.forceSimulation();
    transform = d3.zoomIdentity;

    simulation.force("charge", d3.forceManyBody().strength(-300))
        .force("center", d3.forceCenter(width / 2, height / 2));


  }
  var draw = function draw(containerId, canvasId, graphData) {

    Init(containerId, canvasId, graphData);

    simulation
        .nodes(_graph.nodes)
        .on("tick", ticked);

    simulation.force("link", d3.forceLink().links(_graph.links).id(function(d) {
      return d.id;
    }).strength(0.07).distance(100));

    _graph.links.forEach(createKLink);
    _linkLayer.draw();
    _graph.nodes.forEach(createKNode);
    _nodeLayer.draw();

  };

  function ticked() {
    _graph.links.forEach(moveKLink);
    _linkLayer.draw();

    _graph.nodes.forEach(moveKNode);
    _nodeLayer.draw();
  }

  function createKNode(d) {
    var circle = new Konva.Circle({
      id: d.id,
      x: d.x,
      y: d.y,
      radius: 10,
      fill: color(d.group),
      draggable: true,
      d: d
    });
    circle.on('dragstart', function(e) {
      //this.moveTo(dragLayer);
      this.attrs.d.fx = this.attrs.d.x;
      this.attrs.d.fy = this.attrs.d.y;
    });
    circle.on('dragmove', function(e) {
      this.attrs.d.fx = this.attrs.x;
      this.attrs.d.fy = this.attrs.y;
    });
    circle.on('dragend', function(e) {
      //this.moveTo(_nodeLayer);
      //dragLayer.clear();
      this.attrs.d.fx = null;
      this.attrs.d.fy = null;
      simulation.alphaTarget(0.3).restart();
    });
    circle.on('click', function () {
      //console.log('Mouseover circle');
      //circle.fill = '#000000';
      //var fill = this.fill() == 'red' ? '#00d00f' : 'red';
      var fill = '#000000';

      this.fill(fill);
      _nodeLayer.draw();
    });

    _nodeLayer.add(circle);
  }

  function nodeIdentity(d) {
    return d.id;
  }
  function kNodeFor(d) {
    return _nodeLayer.findOne('#' + d.id);
  }

  function linkIdentity(d) {
    return d.source.id + '_' + d.target.id;
  }

  function createKLink(d) {
    var line = new Konva.Line({
      id: linkIdentity(d),
      points: [
        d.source.x, d.source.y,
        d.target.x, d.target.y
      ],
      stroke: '#aaa',
      strokeWidth: 1,
      lineCap: 'round',
      lineJoin: 'round'
    });
    _linkLayer.add(line);
    //_nodeLayer.add(line);
  }

  function moveKNode(d) {
    var circle = kNodeFor(d);
    if (typeof circle !== "undefined")
    {
      circle.position({
        x: d.x,
        y: d.y
      });
    }}

  function raise(shape) {
    console.log('raise', shape);
    shape.shadowEnabled(true);
    shape.shadowOffset({x:2,y:2});
    shape.shadowBlur(2);
    shape.shadowColor('#aaa');
  }

  function lower(shape) {
    console.log('lower', shape);
    shape.shadowEnabled(false);
  }

  function moveKLink(d) {
    var line = _linkLayer.findOne('#' + linkIdentity(d));
    //var line = _nodeLayer.findOne('#' + linkIdentity(d));

    line.points([
      d.source.x, d.source.y,
      d.target.x, d.target.y
    ])
  }
  var scaleBy = 1.2;

  window.addEventListener('wheel', function (e) {
    e.preventDefault();
  var oldScale = _stage.scaleX();

  var mousePointTo = {
    x: _stage.getPointerPosition().x / oldScale - _stage.x() / oldScale,
    y: _stage.getPointerPosition().y / oldScale - _stage.y() / oldScale,
  };

  var newScale = e.deltaY < 0 ? oldScale * scaleBy : oldScale / scaleBy;
  _stage.scale({ x: newScale, y: newScale });

  var newPos = {
    x: -(mousePointTo.x - _stage.getPointerPosition().x / newScale) * newScale,
    y: -(mousePointTo.y - _stage.getPointerPosition().y / newScale) * newScale
  };
  _stage.position(newPos);
  _stage.batchDraw();
});

  return{
    draw: draw
  }
}(window));