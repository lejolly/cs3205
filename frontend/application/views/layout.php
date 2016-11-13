<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <!--<link rel="icon" href="../../favicon.ico">-->

    <title><?php echo isset($title) ? $title : '<no title>'; ?></title>

    <!-- Bootstrap core CSS -->
    <?php echo link_tag('css/bootstrap.min.css'); ?>

    <!-- Custom styles for this template -->
    <?php echo link_tag('css/style.css'); ?>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">CS3205 Secure Logistics System</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
          <?php $this->load->view('menu/menu'); ?>
        </div><!--/.nav-collapse -->
      </div>
    </nav>

    <div class="container" id="wrapper">
      <div class="row" id="alerts"><?php echo isset($_SESSION['flash']) ? $_SESSION['flash'] : ''; unset($_SESSION['flash']); ?></div>
      <div class="row" id="content"><?php echo isset($contents) ? $contents : '<h1>No Content</h1>'; ?></div>
    </div><!-- /.container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<?php echo base_url().'js/jquery-3.1.1.min.js'; ?>" type="text/javascript"></script>
    <script src="<?php echo base_url().'js/bootstrap.min.js'; ?>" type="text/javascript"></script>
  </body>
</html>
