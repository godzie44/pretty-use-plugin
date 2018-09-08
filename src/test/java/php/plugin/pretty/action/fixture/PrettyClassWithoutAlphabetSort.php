<?php

use Symfony\Component\HttpKernel\Kernel;

use Shared\WidgetBundle\Entity\Widget;
use Shared\CallbackBundle\Dto\Service\Callback\Result\UpdateCallbackDto as UpdateCallbackResult;

use Zzzz;
use Telephony\ApiBundle\Dto\Controller\Callback\Request\CreateCallbackDto;
use JMS\DiExtraBundle\Annotation as DI;

class PrettyClass {
    public function bar()
    {
        echo 'bar';
    }
}