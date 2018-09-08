<?php

use Symfony\Component\HttpKernel\Kernel;

use Shared\WidgetBundle\Entity\Widget;


use Telephony\ApiBundle\Request\RequestDto;
use JMS\DiExtraBundle\Annotation as DI;

class PrettyClass {
    public function bar()
    {
        echo 'bar';
    }
}