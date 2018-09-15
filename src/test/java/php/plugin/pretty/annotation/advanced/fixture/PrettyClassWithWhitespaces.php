<?php

use Symfony\Component\HttpKernel\Kernel;

use Shared\WidgetBundle\Entity\Widget;


use JMS\DiExtraBundle\Annotation as DI;
use Telephony\ApiBundle\Request\RequestDto;

class PrettyClass {
    public function bar()
    {
        echo 'bar';
    }
}