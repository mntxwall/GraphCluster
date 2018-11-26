(function () {
    sigma.canvas.nodes.square = function(node, context, settings) {
        var prefix = settings('prefix') || '',
            size = node[prefix + 'size'];

        //console.log("size is " + size)

        context.fillStyle = node.color || settings('defaultNodeColor');
        context.beginPath();
        /*context.rect(
            node[prefix + 'x'] - size,
            node[prefix + 'y'] - size,
            size * 2,
            size * 2
        );*/
        context.arc(
            node[prefix + 'x'],
            node[prefix + 'y'],
            node[prefix + 'size'],
            0,
            Math.PI,
            false
        );

        context.closePath();
        context.fillStyle = 'red';
        context.fill();

        context.beginPath();
        /*context.rect(
            node[prefix + 'x'] - size,
            node[prefix + 'y'] - size,
            size * 2,
            size * 2
        );*/
        context.arc(
            node[prefix + 'x'],
            node[prefix + 'y'],
            node[prefix + 'size'],
            0,
            Math.PI,
            true
        );

        context.closePath();
        context.fillStyle = 'yellow';
        context.fill();

    };
})();